-- ====================================================================
-- FinSync Seed Categories - V2__seed_default_categories.sql
-- Default taxonomy for all new users per FR-014
-- ====================================================================

-- Expense Categories
INSERT INTO categories (id, user_id, name, direction, icon, color, scope) VALUES
(gen_random_uuid(), NULL, 'Ăn uống', 'EXPENSE', 'restaurant', '#FF5722', 'SYSTEM'),
(gen_random_uuid(), NULL, 'Di chuyển', 'EXPENSE', 'directions_car', '#2196F3', 'SYSTEM'),
(gen_random_uuid(), NULL, 'Mua sắm', 'EXPENSE', 'shopping_bag', '#E91E63', 'SYSTEM'),
(gen_random_uuid(), NULL, 'Nhà cửa & Hóa đơn', 'EXPENSE', 'home', '#9C27B0', 'SYSTEM'),
(gen_random_uuid(), NULL, 'Giải trí', 'EXPENSE', 'movie', '#FF9800', 'SYSTEM'),
(gen_random_uuid(), NULL, 'Y tế & Sức khỏe', 'EXPENSE', 'local_hospital', '#4CAF50', 'SYSTEM'),
(gen_random_uuid(), NULL, 'Giáo dục', 'EXPENSE', 'school', '#3F51B5', 'SYSTEM'),
(gen_random_uuid(), NULL, 'Chi tiêu khác', 'EXPENSE', 'more_horiz', '#607D8B', 'SYSTEM')
ON CONFLICT DO NOTHING;

-- Income Categories
INSERT INTO categories (id, user_id, name, direction, icon, color, scope) VALUES
(gen_random_uuid(), NULL, 'Lương', 'INCOME', 'payments', '#4CAF50', 'SYSTEM'),
(gen_random_uuid(), NULL, 'Thưởng', 'INCOME', 'card_giftcard', '#8BC34A', 'SYSTEM'),
(gen_random_uuid(), NULL, 'Đầu tư & Tiền lãi', 'INCOME', 'trending_up', '#009688', 'SYSTEM'),
(gen_random_uuid(), NULL, 'Thu nhập khác', 'INCOME', 'attach_money', '#00BCD4', 'SYSTEM')
ON CONFLICT DO NOTHING;
