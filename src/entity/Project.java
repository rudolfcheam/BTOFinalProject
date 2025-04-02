package entity;

import java.time.LocalDate;
import java.util.*;


public class Project {
    private String name;
    private String neighbourhood;
    private boolean visible;
    private HDBManager manager;
    private Map<String, Integer> flatCounts = new HashMap<>();
    private Map<String, Integer> flatPrices = new HashMap<>();
    private List<HDBOfficer> officerList = new ArrayList<>();
    private LocalDate startDate, endDate;
    private int officerSlots;


    public Project(String name, String neighbourhood, HDBManager manager, int twoRoomCount, int twoRoomPrice, int threeRoomCount, int threeRoomPrice, int officerSlots) {
        this.name = name;
        this.neighbourhood = neighbourhood;
        this.manager = manager;
        this.visible = true;
        flatCounts.put("2-Room", twoRoomCount);
        flatCounts.put("3-Room", threeRoomCount);
        flatPrices.put("2-Room", twoRoomPrice);
        flatPrices.put("3-Room", threeRoomPrice);
        manager.setProject(this);
        this.officerSlots = officerSlots;
    }

    public String getName() { return name; }
    public String getNeighborhood() { return neighbourhood; }
    public boolean isVisible() { return visible; }
    public HDBManager getManager() { return manager; }
    public Map<String, Integer> getFlatCounts() { return flatCounts; }
    public Map<String, Integer> getFlatPrices() { return flatPrices; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }

    public void setNeighbourhood(String newName) { this.neighbourhood = newName; }
    public void setName(String name) { this.name = name; }
    public void setVisible(boolean visible) { this.visible = visible; }

    public void setFlatCounts(String flatType, int count) {
        if (flatType.equalsIgnoreCase("2-room")) {
            flatCounts.put("2-Room", count);
        } else if (flatType.equalsIgnoreCase("3-room")) {
            flatCounts.put("3-Room", count);
        }
    }

    public void setFlatPrices(String flatType, int price) {
        if (flatType.equalsIgnoreCase("2-room")) {
            flatPrices.put("2-Room", price);
        } else if (flatType.equalsIgnoreCase("3-room")) {
            flatPrices.put("3-Room", price);
        }
    }


    public void setManager(HDBManager newManager) {
        this.manager = newManager;
    }


    public boolean bookFlat(String type) {
        if (flatCounts.getOrDefault(type, 0) > 0) {
            flatCounts.put(type, flatCounts.get(type) - 1);
            return true;
        }
        return false;
    }

    public void setDates() {
        Scanner myScanner = new Scanner(System.in);
        System.out.println("===== Application Start Date =====");
        this.startDate = getDateInput(myScanner);
        System.out.println("===== Application End Date =====");
        this.endDate = getDateInput(myScanner);
    }

    private LocalDate getDateInput(Scanner myScanner) {

        System.out.print("Enter day (1-31): ");
        int day = myScanner.nextInt();
        System.out.print("Enter month (1-12): ");
        int month = myScanner.nextInt();
        System.out.print("Enter year: ");
        int year = myScanner.nextInt();
        try {
            LocalDate date = LocalDate.of(year, month, day);
            System.out.println("Date entered: " + date);
            return date;
        } catch (Exception e) {
            System.out.println("Invalid date entered!" + e.getMessage());
            return getDateInput(myScanner);
        }
    }

    public List<HDBOfficer> getOfficersList() { return officerList; }

    public void addOfficer(HDBOfficer officer) {
        officerList.add(officer);
    }

    public void setOfficerSlots(int officerSlots) { this.officerSlots = officerSlots; }
    public int getOfficerSlots() { return officerSlots; }

    @Override
    public String toString() {
        return name + " in " + neighbourhood;
    }
}
