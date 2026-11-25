-- Default system categories for personal & group expense classification.
INSERT INTO categories (id, name, type, icon_key, color_key)
VALUES
    ('11111111-1111-4111-8111-111111111101', 'Food & Dining', 'EXPENSE', 'restaurant', 'orange'),
    ('11111111-1111-4111-8111-111111111102', 'Travel & Transport', 'EXPENSE', 'directions_car', 'blue'),
    ('11111111-1111-4111-8111-111111111103', 'Housing & Utilities', 'EXPENSE', 'home', 'violet'),
    ('11111111-1111-4111-8111-111111111104', 'Groceries', 'EXPENSE', 'shopping_cart', 'green'),
    ('11111111-1111-4111-8111-111111111105', 'Entertainment', 'EXPENSE', 'movie', 'pink'),
    ('11111111-1111-4111-8111-111111111106', 'Shopping', 'EXPENSE', 'shopping_bag', 'indigo'),
    ('11111111-1111-4111-8111-111111111107', 'Health', 'EXPENSE', 'favorite', 'red'),
    ('11111111-1111-4111-8111-111111111108', 'Other', 'EXPENSE', 'category', 'slate'),
    ('22222222-2222-4222-8222-222222222201', 'Salary', 'INCOME', 'payments', 'emerald'),
    ('22222222-2222-4222-8222-222222222202', 'Bonus', 'INCOME', 'card_giftcard', 'teal'),
    ('22222222-2222-4222-8222-222222222203', 'Other Income', 'INCOME', 'attach_money', 'cyan')
ON CONFLICT DO NOTHING;
