package com.jmpharmacyims.classes;

import java.util.Set;

/**
 * A psuedo-enumeration class that contains static classes
 * that represent a category of pseudo-enums for replacing magic numbers in UIManager
 * 
 */
public class MenuOption {
    private MenuOption() {}
    
    private static interface HasLogOut {
        static final int LOGOUT = 0;
    }

    public static class AccountType implements HasLogOut {
        public static final int 
            CUSTOMER = 1, 
            PHARMACY = 2, 
            ADMIN = 3;
        
        public static Set<Integer> getValues() {
            return Set.of(
                CUSTOMER, 
                PHARMACY, 
                ADMIN, 
                LOGOUT
            );
        }
    } 

    public static class CustomerOperation implements HasLogOut {
        public static final int
        BUY_MEDICINE = 1,
        VIEW_ACCOUNT_DETAILS = 2,
        DEPOSIT_FUNDS = 3;

        public static Set<Integer> getValues() {
            return Set.of(
                BUY_MEDICINE, 
                VIEW_ACCOUNT_DETAILS, 
                DEPOSIT_FUNDS, 
                LOGOUT
            );
        }
    } 

    public static class PharmacyOperation implements HasLogOut {
        public static final int
        ADD_MEDICINE = 1,
        SHOW_MEDICINE_LIST = 2,
        UPDATE_MEDICINE_AMOUNT = 3,
        UPDATE_MEDICINE_PRICE = 4,
        DELETE_MEDICINE = 5;

        public static Set<Integer> getValues() {
            return Set.of(
                ADD_MEDICINE, 
                SHOW_MEDICINE_LIST, 
                UPDATE_MEDICINE_AMOUNT, 
                UPDATE_MEDICINE_PRICE,
                DELETE_MEDICINE, 
                LOGOUT
            );
        }
    } 

    public static class AdminOperation implements HasLogOut {
        public static final int
        REGISTER_CUSTOMER = 1,
        SHOW_CUSTOMER_LIST = 2,
        UPDATE_CUSTOMER_CREDENTIALS = 3,
        UPDATE_PHARMACY_CREDENTIALS = 4,
        DELETE_CUSTOMER = 5;

        public static Set<Integer> getValues() {
            return Set.of(
                REGISTER_CUSTOMER, 
                SHOW_CUSTOMER_LIST, 
                UPDATE_CUSTOMER_CREDENTIALS, 
                UPDATE_PHARMACY_CREDENTIALS,
                DELETE_CUSTOMER, 
                LOGOUT
            );
        }
    } 
}
