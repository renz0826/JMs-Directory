package com.example.classes;

import java.util.ArrayList;

public class Pharmacy extends Account {
    private ArrayList<Medicine> medicines = new ArrayList<>();

    // Methods
    public void addMedicine() {}
    public void searchMedicine() {}
    public void updateMedicineAmount() {}
    public void updateMedicinePrice() {}
    public void deleteMedicine() {}

    // Getters
    public ArrayList<Medicine> getMedicines() {
        return medicines;
    }

    // Test methods
    @Override
    public void details() {
        super.details();
        for (Medicine medicine : medicines) {
            medicine.details();
        }
    }
}