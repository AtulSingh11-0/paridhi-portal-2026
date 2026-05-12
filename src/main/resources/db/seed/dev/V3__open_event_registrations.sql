-- =============================================
-- V3: Open all event and event combo registrations
-- =============================================

-- 1. Open registrations for ALL events
UPDATE paridhi_dev_db.events
SET is_event_registration_open = b'1',
    updated_at                 = NOW()
WHERE is_event_registration_open = b'0';

-- 2. Open registrations for ALL event combos
UPDATE paridhi_dev_db.event_combos
SET is_event_combo_registration_open = b'1',
    updated_at                       = NOW()
WHERE is_event_combo_registration_open = b'0';
