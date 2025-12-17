-- =============================================
-- Materialized Views for Database Optimization
-- =============================================

-- 1. Product Catalog Summary Materialized View
-- Aggregates product data for fast catalog browsing
CREATE MATERIALIZED VIEW mv_product_catalog_summary AS
SELECT 
    p.id,
    p.asin,
    p.name,
    p.category,
    p.price,
    p.available,
    p.image_url,
    p.stock,
    COUNT(DISTINCT oi.order_id) as total_orders,
    COALESCE(SUM(oi.quantity), 0) as total_sold
FROM products p
LEFT JOIN order_items oi ON p.id = oi.product_id
GROUP BY p.id, p.asin, p.name, p.category, p.price, p.available, p.image_url, p.stock;

-- Create indexes on materialized view for fast queries
CREATE INDEX idx_mv_catalog_category ON mv_product_catalog_summary(category);
CREATE INDEX idx_mv_catalog_available ON mv_product_catalog_summary(available);
CREATE INDEX idx_mv_catalog_asin ON mv_product_catalog_summary(asin);

-- 2. User Order Summary Materialized View
-- Aggregates user order data for quick dashboard loading
CREATE MATERIALIZED VIEW mv_user_order_summary AS
SELECT 
    u.id as user_id,
    u.email,
    COUNT(DISTINCT o.id) as total_orders,
    COALESCE(SUM(o.total_amount), 0) as total_spent,
    MAX(o.order_date) as last_order_date,
    COUNT(DISTINCT CASE WHEN o.status = 'PENDING' THEN o.id END) as pending_orders,
    COUNT(DISTINCT CASE WHEN o.status = 'CONFIRMED' THEN o.id END) as confirmed_orders,
    COUNT(DISTINCT CASE WHEN o.status = 'SHIPPED' THEN o.id END) as shipped_orders,
    COUNT(DISTINCT CASE WHEN o.status = 'DELIVERED' THEN o.id END) as delivered_orders
FROM users u
LEFT JOIN orders o ON u.id = o.user_id
GROUP BY u.id, u.email;

-- Create indexes on user order summary
CREATE INDEX idx_mv_user_order_user_id ON mv_user_order_summary(user_id);
CREATE INDEX idx_mv_user_order_email ON mv_user_order_summary(email);

-- 3. Popular Products Materialized View
-- Tracks popular products for recommendations
CREATE MATERIALIZED VIEW mv_popular_products AS
SELECT 
    p.id,
    p.asin,
    p.name,
    p.category,
    p.price,
    p.image_url,
    COUNT(DISTINCT oi.order_id) as order_count,
    SUM(oi.quantity) as total_quantity_sold,
    AVG(p.price * oi.quantity) as avg_order_value
FROM products p
INNER JOIN order_items oi ON p.id = oi.product_id
INNER JOIN orders o ON oi.order_id = o.id
WHERE o.order_date >= NOW() - INTERVAL '30 days'
GROUP BY p.id, p.asin, p.name, p.category, p.price, p.image_url
ORDER BY order_count DESC, total_quantity_sold DESC
LIMIT 100;

-- Create indexes on popular products
CREATE INDEX idx_mv_popular_category ON mv_popular_products(category);
CREATE INDEX idx_mv_popular_order_count ON mv_popular_products(order_count DESC);

-- 4. Category Statistics Materialized View
-- Aggregates category-level metrics
CREATE MATERIALIZED VIEW mv_category_statistics AS
SELECT 
    p.category,
    COUNT(DISTINCT p.id) as product_count,
    COUNT(DISTINCT CASE WHEN p.available = true THEN p.id END) as available_products,
    AVG(p.price) as avg_price,
    MIN(p.price) as min_price,
    MAX(p.price) as max_price,
    COALESCE(SUM(oi.quantity), 0) as total_sold,
    COUNT(DISTINCT oi.order_id) as total_orders
FROM products p
LEFT JOIN order_items oi ON p.id = oi.product_id
WHERE p.category IS NOT NULL
GROUP BY p.category;

-- Create index on category statistics
CREATE UNIQUE INDEX idx_mv_category_name ON mv_category_statistics(category);

-- 5. Order Statistics Materialized View
-- Aggregates order statistics by date for analytics
CREATE MATERIALIZED VIEW mv_order_statistics AS
SELECT 
    DATE(o.order_date) as order_date,
    COUNT(DISTINCT o.id) as order_count,
    SUM(o.total_amount) as total_revenue,
    AVG(o.total_amount) as avg_order_value,
    COUNT(DISTINCT o.user_id) as unique_customers,
    COUNT(DISTINCT CASE WHEN o.status = 'DELIVERED' THEN o.id END) as delivered_orders,
    COUNT(DISTINCT CASE WHEN o.status = 'CANCELLED' THEN o.id END) as cancelled_orders
FROM orders o
GROUP BY DATE(o.order_date);

-- Create indexes on order statistics
CREATE UNIQUE INDEX idx_mv_order_stats_date ON mv_order_statistics(order_date DESC);

-- =============================================
-- Indexes on Base Tables for Query Optimization
-- =============================================

-- Products table indexes
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category);
CREATE INDEX IF NOT EXISTS idx_products_available ON products(available);
CREATE INDEX IF NOT EXISTS idx_products_asin ON products(asin);
CREATE INDEX IF NOT EXISTS idx_products_name ON products(name);

-- Orders table indexes
CREATE INDEX IF NOT EXISTS idx_orders_user_id ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_orders_order_date ON orders(order_date DESC);
CREATE INDEX IF NOT EXISTS idx_orders_user_status ON orders(user_id, status);

-- Order Items table indexes
CREATE INDEX IF NOT EXISTS idx_order_items_product_id ON order_items(product_id);
CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items(order_id);

-- Users table indexes
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- OTP table indexes
CREATE INDEX IF NOT EXISTS idx_otp_user_id ON otp(user_id);
CREATE INDEX IF NOT EXISTS idx_otp_expiry ON otp(expiry_time);
