package com.jmpharmacyims.classes;

import com.jmpharmacyims.classes.coreclasses.Database;
import com.jmpharmacyims.classes.uimanager.UIManager;

public class Main {
    public static void main(String[] args) {        
        startInventoryMedicineSystem();
    }

    private static void startInventoryMedicineSystem() {
        Database.validateInitialDataFiles();
        UIManager.chooseAccountMenu();
    }
}
