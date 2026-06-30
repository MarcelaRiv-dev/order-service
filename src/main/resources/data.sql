-- Sample Orders Data
-- Note: createdAt and updatedAt are set via @PrePersist / @PreUpdate in the entity lifecycle callbacks,
-- but for direct SQL inserts we supply explicit timestamps.

INSERT INTO orders (user_id, status, total_amount, shipping_address, notes, created_at, updated_at)
VALUES
    (1, 'DELIVERED',  149.97, '123 Main St, Springfield, IL 62701', 'Please leave at front door', TIMESTAMPADD(DAY, -30, NOW()), TIMESTAMPADD(DAY, -25, NOW())),
    (2, 'SHIPPED',    299.98, '456 Oak Ave, Chicago, IL 60601', NULL, TIMESTAMPADD(DAY, -5, NOW()), TIMESTAMPADD(DAY, -3, NOW())),
    (1, 'CONFIRMED',   89.99, '123 Main St, Springfield, IL 62701', 'Gift wrapping requested', TIMESTAMPADD(DAY, -2, NOW()), TIMESTAMPADD(DAY, -1, NOW())),
    (3, 'PENDING',    199.95, '789 Elm Rd, Naperville, IL 60540', NULL, TIMESTAMPADD(HOUR, -3, NOW()), TIMESTAMPADD(HOUR, -3, NOW())),
    (2, 'CANCELLED',   49.99, '456 Oak Ave, Chicago, IL 60601', 'Customer requested cancellation', TIMESTAMPADD(DAY, -10, NOW()), TIMESTAMPADD(DAY, -9, NOW()));

-- Order Items for Order 1 (DELIVERED)
INSERT INTO order_items (order_id, product_id, product_name, quantity, unit_price, subtotal)
VALUES
    (1, 1, 'Wireless Headphones', 1, 79.99, 79.99),
    (1, 3, 'USB-C Hub', 2, 34.99, 69.98);

-- Order Items for Order 2 (SHIPPED)
INSERT INTO order_items (order_id, product_id, product_name, quantity, unit_price, subtotal)
VALUES
    (2, 2, 'Mechanical Keyboard', 1, 149.99, 149.99),
    (2, 4, 'Mouse Pad XL', 3, 49.99, 149.99);

-- Order Items for Order 3 (CONFIRMED)
INSERT INTO order_items (order_id, product_id, product_name, quantity, unit_price, subtotal)
VALUES
    (3, 5, 'Webcam HD 1080p', 1, 89.99, 89.99);

-- Order Items for Order 4 (PENDING)
INSERT INTO order_items (order_id, product_id, product_name, quantity, unit_price, subtotal)
VALUES
    (4, 1, 'Wireless Headphones', 1, 79.99,  79.99),
    (4, 6, 'Laptop Stand',        2, 59.98, 119.96);

-- Order Items for Order 5 (CANCELLED)
INSERT INTO order_items (order_id, product_id, product_name, quantity, unit_price, subtotal)
VALUES
    (5, 7, 'Screen Cleaner Kit', 1, 49.99, 49.99);
