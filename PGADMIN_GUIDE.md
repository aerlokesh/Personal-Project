# pgAdmin 4 Setup Guide

## What is pgAdmin?

pgAdmin4 is a free, open-source web-based GUI tool for managing PostgreSQL databases. It allows you to:
- Browse databases, tables, and data visually
- Run SQL queries
- Create and modify tables
- Monitor database performance
- Export/import data

## Connection Setup

### Step 1: First Launch
When pgAdmin4 first opens, it will:
1. Open in your browser automatically
2. Ask you to set a master password (used to encrypt saved passwords)
3. Set any password you like - this is just for pgAdmin, not PostgreSQL

### Step 2: Add Server Connection

1. **Right-click "Servers"** in the left sidebar
2. Select **"Register" → "Server"**

### Step 3: Configure Connection

**General Tab:**
- Name: `Local PostgreSQL` (or any name you prefer)

**Connection Tab:**
- Host name/address: `localhost`
- Port: `5432`
- Maintenance database: `postgres`
- Username: `aerloki` (your macOS username)
- Password: *Leave empty* (no password for local Homebrew PostgreSQL)
- Save password: ✅ Check this box

**Advanced Tab** (optional):
- DB restriction: `ecommerce` (to show only your e-commerce database)

Click **Save**

## Browsing Your E-commerce Database

Once connected, navigate through the tree:

```
Servers
└── Local PostgreSQL (localhost:5432)
    └── Databases
        └── ecommerce
            └── Schemas
                └── public
                    └── Tables
                        ├── users (3 test users)
                        ├── products (12 products with ASINs)
                        ├── orders (empty for now)
                        └── order_items (empty for now)
```

## Viewing Data

### To view table data:
1. Expand **Tables** in the tree
2. Right-click on a table (e.g., `products`)
3. Select **"View/Edit Data" → "All Rows"**

### To run SQL queries:
1. Click on database name `ecommerce`
2. Click **Tools** → **Query Tool** (or press `Alt+Shift+Q`)
3. Type your query:
   ```sql
   -- View all products
   SELECT * FROM products;
   
   -- View products by category
   SELECT * FROM products WHERE category = 'Electronics';
   
   -- View all users
   SELECT * FROM users;
   
   -- Search products
   SELECT * FROM products WHERE name ILIKE '%kindle%';
   ```
4. Click **Execute** (▶️ button) or press `F5`

## Useful Queries for Your E-commerce App

```sql
-- Count products by category
SELECT category, COUNT(*) as count 
FROM products 
GROUP BY category 
ORDER BY count DESC;

-- Products under $50
SELECT name, price, asin 
FROM products 
WHERE price < 50 
ORDER BY price;

-- Products with low stock (under 100)
SELECT name, stock, category 
FROM products 
WHERE stock < 100 
ORDER BY stock;

-- View user details
SELECT email, name, address 
FROM users;

-- Find product by ASIN
SELECT * FROM products 
WHERE asin = 'B08N5WRWNW';
```

## Adding Your Own Products

You can add products directly in pgAdmin:

1. Right-click **products** table
2. Select **"View/Edit Data" → "All Rows"**
3. In the data grid, click **"+"** button (Add row)
4. Fill in the fields:
   - ASIN: Unique identifier (e.g., `B0CUSTOM1`)
   - Name: Product name
   - Description: Product description
   - Price: Decimal price (e.g., `29.99`)
   - Stock: Integer quantity
   - Category: Category name
   - Image URL: Link to product image
   - Available: `true` or `false`
5. Click **Save** (💾 button)

## Tips

- **Refresh button** (🔄): Refreshes the tree view
- **SQL tab**: Shows SQL queries pgAdmin generates
- **Messages tab**: Shows query results and errors
- **Export**: Right-click table → **Import/Export** to export to CSV
- **Backup**: Right-click database → **Backup** to create backups

## Troubleshooting

### "Could not connect to server"
- PostgreSQL service not running
- Solution: `brew services start postgresql@16`

### "Password authentication failed"
- Wrong username in connection settings
- Use username: `aerloki` (your macOS user)
- Leave password blank

### "Database does not exist"
- Navigate to `postgres` database first
- Then look for `ecommerce` database in the list

## Quick Access

**pgAdmin URL**: http://localhost:52607 (or similar, opens automatically)
**Database**: ecommerce
**Host**: localhost:5432
**Username**: aerloki
**Password**: (none)

## Your Database Structure

```
products table:
- id (bigint, auto-increment)
- asin (varchar, unique)
- name (varchar)
- description (text)
- price (decimal)
- stock (integer)
- image_url (varchar)
- category (varchar)
- available (boolean)

users table:
- id (bigint, auto-increment)
- email (varchar, unique)
- name (varchar)
- password (varchar)
- address (varchar)
- phone_number (varchar)

orders table:
- id (bigint, auto-increment)
- user_id (bigint, foreign key)
- total_amount (decimal)
- order_date (timestamp)
- status (enum)
- shipping_address (varchar)

order_items table:
- id (bigint, auto-increment)
- order_id (bigint, foreign key)
- product_id (bigint, foreign key)
- quantity (integer)
- price (decimal)
