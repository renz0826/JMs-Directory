

# JM's Directory: A Medicine Inventory System 🚑💊
A lightweight Java console application for small pharmacies. Three roles: Customer, Pharmacy, and Admin. Stores data in a JSON file format, so it runs anywhere with a JDK.


## Key Features ✨
- Implements Basic Operations: Create, Read, Update, Delete
- User accounts: login, view medicines, deposit funds
- Pharmacy inventory: add / edit / delete medicine
- Simple import/export: JSON import/export for medicines.


## Run Locally (compile & run) ▶️
Requirements: Java 24 (JDK). 


## Console Commands / User Flows 🧭
- Startup menu:
  - [1] User Login
  - [2] Pharmacy Login
  - [3] Admin Login
  - [0] Exit


- Customer Menu:
  - [1] Buy Medicine
  - [2] View account details
  - [3] Deposit funds
  - [0] Logout


- Pharmacy Menu:
  - [1] Add Medicine
  - [2] Show List of Medicines
  - [3] Restock Medicine
  - [4] Update Medicine Price
  - [5] Delete Medicine
  - [0] Logout
















- Admin Menu:
  - [1] Register a Customer
  - [2] Show List of Customers
  - [3] Edit Customer Credentials
  - [4] Edit Customer Credentials
  - [5] Delete Customer
  - [0] Logout






## Account Login Credentials 👤🔐

```json
Customer
  "username" : "Gem",
  "password" : "GemGem",
```


```json
Pharmacy
  "username" : "user",
  "password" : "password",
```


```json
Admin
    "username": "admin",
    "password": "admin123"
```



## Test Data for Expired Medicine
```json
"medicines": [
  {
    "name": "Carbocisteine",
    "brand": "Solmux",
    "purpose": "Cough with phlegm relief",
    "expirationDate": "12/5/2024",
    "amount": 50,
    "price": 11.0
  },
  {
    "name": "Phenylpropanolamine",
    "brand": "Neozep",
    "purpose": "Nasal decongestant",
    "expirationDate": "1/1/2023",
    "amount": 100,
    "price": 6.0
  },
  {
    "name": "Mefenamic Acid",
    "brand": "Gardan",
    "purpose": "Pain relief",
    "expirationDate": "15/10/2024",
    "amount": 30,
    "price": 8.5
  },
  {
    "name": "Loratadine",
    "brand": "Claritin",
    "purpose": "Allergy relief",
    "expirationDate": "4/4/2022",
    "amount": 60,
    "price": 25.0
  },
  {
    "name": "Aluminum Hydroxide",
    "brand": "Maalox",
    "purpose": "Antacid",
    "expirationDate": "20/9/2023",
    "amount": 25,
    "price": 10.5
  },
  {
    "name": "Dicycloverine",
    "brand": "Bentyl",
    "purpose": "Irritable bowel relief",
    "expirationDate": "5/2/2025",
    "amount": 45,
    "price": 14.0
  }
]
```