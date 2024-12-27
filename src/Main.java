import java.security.DrbgParameters.Reseed;
import java.util.*;

// Superclass - Vehicle
class Vehicle {
    private String vehicleId;
    private String brand;
    private String model;
    private double basePricePerDay;
    private boolean isAvailable;
    private boolean underMaintenance;

    public Vehicle(String vehicleId, String brand, String model, double basePricePerDay) {
        this.vehicleId = vehicleId;
        this.brand = brand;
        this.model = model;
        this.basePricePerDay = basePricePerDay;
        this.isAvailable = true;
        this.underMaintenance = false;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public double calculatePrice(int rentalDays) {
        return basePricePerDay * rentalDays;
    }

    public boolean isAvailable() {
        return isAvailable && !underMaintenance;
    }

    public void rent() {
        isAvailable = false;
    }

    public void returnVehicle() {
        isAvailable = true;
    }

    public void setMaintenance(boolean underMaintenance) {
        this.underMaintenance = underMaintenance;
    }

    public boolean isUnderMaintenance() {
        return underMaintenance;
    }
}

// Subclass representing a Car
class Car extends Vehicle {
    public Car(String vehicleId, String brand, String model, double basePricePerDay) {
        super(vehicleId, brand, model, basePricePerDay);
    }
}

// Subclass representing a Luxury Car
class LuxuryCar extends Vehicle {
    public LuxuryCar(String vehicleId, String brand, String model, double basePricePerDay) {
        super(vehicleId, brand, model, basePricePerDay);
    }

    @Override
    public double calculatePrice(int rentalDays) {
        double luxuryTax = 50.0; // Flat luxury tax per rental
        return super.calculatePrice(rentalDays) + luxuryTax;
    }
}

// Class representing a Rental
class Rental {
    private Vehicle vehicle;
    private Customer customer;
    private int days;

    public Rental(Vehicle vehicle, Customer customer, int days) {
        this.vehicle = vehicle;
        this.customer = customer;
        this.days = days;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public Customer getCustomer() {
        return customer;
    }

    public int getDays() {
        return days;
    }
}

// Class representing a Customer
class Customer {
    private String customerId;
    private String name;
    private List<Rental> rentalHistory;

    public Customer(String customerId, String name) {
        this.customerId = customerId;
        this.name = name;
        this.rentalHistory = new ArrayList<>();
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public List<Rental> getRentalHistory() {
        return rentalHistory;
    }

    public void addRental(Rental rental) {
        rentalHistory.add(rental);
    }
}


// CarRentalSystem Class
class CarRentalSystem {
    private List<Vehicle> vehicles;
    private List<Customer> customers;
    private List<Rental> rentals;

    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String BLUE = "\u001B[34m";
    private static final String YELLOW = "\u001B[33m";

    public CarRentalSystem() {
        vehicles = new ArrayList<>();
        customers = new ArrayList<>();
        rentals = new ArrayList<>();
    }

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    public void rentVehicle(Vehicle vehicle, Customer customer, int days) {
        if (vehicle.isAvailable()) {
            vehicle.rent();
            Rental rental = new Rental(vehicle, customer, days);
            rentals.add(rental);
            customer.addRental(rental);
            System.out.printf(GREEN+"Successfully rented %s %s to %s for %d days.%n"+RESET,
                vehicle.getBrand(), vehicle.getModel(), customer.getName(), days);
        } else {
            System.out.println(RED+"Vehicle is not available for rent."+RESET);
        }
    }

    public void returnVehicle(Vehicle vehicle) {
        vehicle.returnVehicle();
        rentals.removeIf(rental -> rental.getVehicle().equals(vehicle));
        System.out.println(GREEN+"Vehicle returned successfully."+RESET);
    }

    public void markVehicleForMaintenance(String vehicleId) {
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getVehicleId().equals(vehicleId)) {
                vehicle.setMaintenance(true);
                System.out.println(YELLOW+"Vehicle marked as under maintenance."+RESET);
                return;
            }
        }
        System.out.println(RED+"Vehicle not found."+RESET);
    }

    public void displayAvailableVehicles() {
        System.out.println(BLUE+"\nAvailable Vehicles:"+RESET);
        for (Vehicle vehicle : vehicles) {
            if (vehicle.isAvailable()) {
                System.out.printf("%s - %s %s ($%.2f/day)%n",
                    vehicle.getVehicleId(), vehicle.getBrand(), vehicle.getModel(), vehicle.calculatePrice(1));
            }
        }
    }

    public void searchVehicles(String query) {
        System.out.println(BLUE+"\nSearch Results:"+RESET);
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getBrand().toLowerCase().contains(query.toLowerCase())
                || vehicle.getModel().toLowerCase().contains(query.toLowerCase())) {
                System.out.printf("%s - %s %s ($%.2f/day)%n",
                    vehicle.getVehicleId(), vehicle.getBrand(), vehicle.getModel(), vehicle.calculatePrice(1));
            }
        }
    }

    public void displayRentalHistory(String customerId) {
        for (Customer customer : customers) {
            if (customer.getCustomerId().equals(customerId)) {
                System.out.printf(BLUE+"\nRental History for %s:%n"+RESET, customer.getName());
                for (Rental rental : customer.getRentalHistory()) {
                    System.out.printf("%s %s for %d days%n",
                        rental.getVehicle().getBrand(), rental.getVehicle().getModel(), rental.getDays());
                }
                return;
            }
        }
        System.out.println(RED+"Customer not found."+RESET);
    }

    public void mainMenu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println(BLUE+"\n===== Car Rental System ====="+RESET);
            System.out.println("1. Manage Customers");
            System.out.println("2. Manage Vehicles");
            System.out.println("3. Manage Rentals");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    customerMenu(scanner);
                    break;
                case 2:
                    vehicleMenu(scanner);
                    break;
                case 3:
                    rentalMenu(scanner);
                    break;
                case 4:
                    System.out.println(GREEN+"Thank you for using the Car Rental System!"+RESET);
                    return;
                default:
                    System.out.println(RED+"Invalid choice. Please try again."+RESET);
            }
        }
    }

    private void customerMenu(Scanner scanner) {
        System.out.println(BLUE+"\n===== Manage Customers ====="+RESET);
        System.out.println("1. Add Customer");
        System.out.println("2. View Rental History");
        System.out.println("3. Back to Main Menu");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                System.out.print("Enter Customer Name: ");
                String name = scanner.nextLine();
                String customerId = "CUST" + (customers.size() + 1);
                customers.add(new Customer(customerId, name));
                System.out.printf(GREEN+"Customer added with ID: %s%n"+RESET, customerId);
                break;
            case 2:
                System.out.print("Enter Customer ID: ");
                String id = scanner.nextLine();
                displayRentalHistory(id);
                break;
            case 3:
                return;
            default:
                System.out.println(RED+"Invalid choice. Please try again."+RESET);
        }
    }

    private void vehicleMenu(Scanner scanner) {
        System.out.println(BLUE+"\n===== Manage Vehicles ====="+RESET);
        System.out.println("1. View Available Vehicles");
        System.out.println("2. Mark Vehicle for Maintenance");
        System.out.println("3. Back to Main Menu");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                displayAvailableVehicles();
                break;
            case 2:
                System.out.print("Enter Vehicle ID: ");
                String vehicleId = scanner.nextLine();
                markVehicleForMaintenance(vehicleId);
                break;
            case 3:
                return;
            default:
                System.out.println(RED+"Invalid choice. Please try again."+RESET);
        }
    }

    private void rentalMenu(Scanner scanner) {
        System.out.println(BLUE+"\n===== Manage Rentals ====="+RESET);
        System.out.println("1. Rent a Vehicle");
        System.out.println("2. Return a Vehicle");
        System.out.println("3. Back to Main Menu");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                System.out.print("Enter Customer ID: ");
                String customerId = scanner.nextLine();
                Customer customer = customers.stream()
                    .filter(c -> c.getCustomerId().equals(customerId))
                    .findFirst().orElse(null);
                if (customer == null) {
                    System.out.println(RED+"Customer not found."+RESET);
                    break;
                }
                displayAvailableVehicles();
                System.out.print("Enter Vehicle ID: ");
                String vehicleId = scanner.nextLine();
                System.out.print("Enter Rental Days: ");
                int days = scanner.nextInt();
                scanner.nextLine();
                Vehicle vehicle = vehicles.stream()
                    .filter(v -> v.getVehicleId().equals(vehicleId) && v.isAvailable())
                    .findFirst().orElse(null);
                if (vehicle == null) {
                    System.out.println(RED+"Vehicle not available."+RESET);
                    break;
                }
                rentVehicle(vehicle, customer, days);
                break;
            case 2:
                System.out.print("Enter Vehicle ID: ");
                vehicleId = scanner.nextLine();
                Vehicle returnVehicle = vehicles.stream()
                    .filter(v -> v.getVehicleId().equals(vehicleId))
                    .findFirst().orElse(null);
                if (returnVehicle == null) {
                    System.out.println(RED+"Invalid Vehicle ID."+RESET);
                    break;
                }
                returnVehicle(returnVehicle);
                break;
            case 3:
                return;
            default:
                System.out.println(RED+"Invalid choice. Please try again."+RESET);
        }
    }
}

// Main class
public class Main {
    public static void main(String[] args) {
        CarRentalSystem rentalSystem = new CarRentalSystem();

        // Adding vehicles
        rentalSystem.addVehicle(new Car("C001", "Toyota", "Camry", 60.0));
        rentalSystem.addVehicle(new Car("C002", "Honda", "Accord", 70.0));
        rentalSystem.addVehicle(new LuxuryCar("LC001", "Mercedes", "S-Class", 200.0));
        rentalSystem.addVehicle(new LuxuryCar("LC002", "BMW", "7 Series", 250.0));

        rentalSystem.mainMenu();
    }
}
