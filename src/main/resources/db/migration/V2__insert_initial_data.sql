-- INSERT initial data into the events table

INSERT INTO paridhi_dev_db.events (created_at, created_by, updated_at, updated_by, domain, event_coordinator_details,
                                   event_date, event_description, event_max_players, event_min_players, event_name,
                                   event_poster_file_id, event_poster_url, event_registration_fee, event_rule_book_url,
                                   event_type, event_venue, is_event_registration_open, is_team_event)

VALUES ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'GAMING', '[
  "Aditya Kumar - 9823456789",
  "Priya Sharma - 9812365478"
]', '2026-05-15 10:00:00.000000', 'Squad battle royale.', 5, 4, 'BGMI', '69f9043f5c7cd75eb85be3c9',
        'https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/event-posters/gaming-bgmi',
        150.00, NULL, 'MAIN', 'MSIT Campus', b'0', b'1'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'GAMING', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Football esports.', 1, 1, 'E-Football (PES)', '69f9043e5c7cd75eb85be39b',
        'https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/event-posters/gaming-pes',
        150.00, NULL, 'MAIN', 'MSIT Campus', b'0', b'0'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'GAMING', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Tactical shooter.', 5, 5, 'Valorant', '69f9043e5c7cd75eb85be27e',
        'https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/event-posters/gaming-valorant',
        150.00, NULL, 'MAIN', 'MSIT Campus', b'0', b'1'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'GAMING', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Fighting matches.', 1, 1, 'Mortal Kombat', '69f9043e5c7cd75eb85be1d8',
        'https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/event-posters/gaming-main',
        150.00, NULL, 'ON_SPOT', 'MSIT Campus', b'0', b'0'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'GAMING', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Football knockout.', 1, 1, 'EA FC 25', '69f9043e5c7cd75eb85be1d8',
        'https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/event-posters/gaming-main',
        150.00, NULL, 'ON_SPOT', 'MSIT Campus', b'0', b'0'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'GENERAL', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Carrom board game.', 1, 1, 'Carrom', '69f9043e5c7cd75eb85be3ad',
        'https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/event-posters/general-main',
        50.00, NULL, 'MAIN', 'MSIT Campus', b'1', b'0'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'GENERAL', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Quiz event.', 2, 1, 'Binge Quiz', '69f9043e5c7cd75eb85be3ad',
        'https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/event-posters/general-main',
        50.00, NULL, 'MAIN', 'MSIT Campus', b'1', b'1'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'GENERAL', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Chess battles.', 1, 1, 'Chess', '69f9043e5c7cd75eb85be3ad',
        'https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/event-posters/general-main',
        50.00, NULL, 'ON_SPOT', 'MSIT Campus', b'1', b'0'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'GENERAL', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Dart accuracy.', 1, 1, 'Dart', '69f9043e5c7cd75eb85be3ad',
        'https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/event-posters/general-main',
        50.00, NULL, 'ON_SPOT', 'MSIT Campus', b'1', b'0'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'CIVIL', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Bridge building challenge.', 3, 2, 'Setu Bandhan', '69f9043e5c7cd75eb85be20a',
        'https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/event-posters/civil-main',
        80.00, NULL, 'MAIN', 'MSIT Campus', b'1', b'1'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'CIVIL', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Tower building event.', 3, 2, 'TOT', '69f9043e5c7cd75eb85be20a',
        'https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/event-posters/civil-main',
        80.00, NULL, 'MAIN', 'MSIT Campus', b'1', b'1'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'CIVIL', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Architectural design.', 2, 1, 'MegaArch', '69f9043e5c7cd75eb85be20a',
        'https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/event-posters/civil-main',
        80.00, NULL, 'MAIN', 'MSIT Campus', b'1', b'1'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'CIVIL', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'CAD competition.', 1, 1, 'CAD Clash', '69f9043e5c7cd75eb85be20a',
        'https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/event-posters/civil-main',
        80.00, NULL, 'MAIN', 'MSIT Campus', b'1', b'0'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'CODING', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Algorithm contest.', 1, 1, 'Problematrix', '69f9043f5c7cd75eb85be3e9',
        'https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/event-posters/coding-main',
        100.00, NULL, 'MAIN', 'MSIT Campus', b'1', b'0'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'CODING', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Security coding.', 1, 1, 'CipherSiege', '69f9043f5c7cd75eb85be3e9',
        'https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/event-posters/coding-main',
        100.00, NULL, 'MAIN', 'MSIT Campus', b'1', b'0'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'CODING', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Debugging contest.', 1, 1, 'DéjàView', '69f9043f5c7cd75eb85be3e9',
        'https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/event-posters/coding-main',
        100.00, NULL, 'MAIN', 'MSIT Campus', b'1', b'0'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'CODING', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Quick coding.', 1, 1, 'Flash Code', '69f9043f5c7cd75eb85be3e9',
        'https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/event-posters/coding-main',
        100.00, NULL, 'ON_SPOT', 'MSIT Campus', b'1', b'0'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'CODING', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'StackOverflow style.', 1, 1, 'HeapOverflow', '69f9043f5c7cd75eb85be3e9',
        'https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/event-posters/coding-main',
        100.00, NULL, 'ON_SPOT', 'MSIT Campus', b'1', b'0'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'ELECTRICAL', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Circuit design.', 2, 1, 'Circuitron', '69f9043e5c7cd75eb85bde8f',
        'https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/event-posters/electrical-main',
        80.00, NULL, 'MAIN', 'MSIT Campus', b'1', b'1'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'ELECTRICAL', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Quick circuits.', 1, 1, 'Circuit Byte', '69f9043e5c7cd75eb85bde8f',
        'https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/event-posters/electrical-main',
        80.00, NULL, 'ON_SPOT', 'MSIT Campus', b'1', b'0'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'ROBOTICS', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Robot war.', 4, 2, 'Armageddon (War)', '69f9043e5c7cd75eb85bddf2',
        'https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/event-posters/robotics-main',
        200.00, NULL, 'MAIN', 'MSIT Campus', b'1', b'1'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'ROBOTICS', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Robot race.', 3, 2, 'Dare to Desiny (Race)', '69f9043e5c7cd75eb85bddf2',
        'https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/event-posters/robotics-main', 200.00, NULL,
        'MAIN', 'MSIT Campus', b'1', b'1'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'ROBOTICS', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Robot soccer.', 4, 3, 'Dribble Drive (Soccer)', '69f9043e5c7cd75eb85bddf2',
        'https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/event-posters/robotics-main', 200.00, NULL,
        'MAIN', 'MSIT Campus', b'1', b'1'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'ROBOTICS', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Line follower.', 2, 1, 'Shadow Tracker (LFR)', '69f9043e5c7cd75eb85bddf2',
        'https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/event-posters/robotics-main',
        200.00, NULL, 'MAIN', 'MSIT Campus', b'1', b'1'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'ROBOTICS', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', '1v1 race.', 2, 2, 'TwinTurbo (Race 1v1)', '69f9043e5c7cd75eb85bddf2',
        'https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/event-posters/robotics-main', 200.00, NULL,
        'ON_SPOT',
        'MSIT Campus', b'1', b'1'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'ROBOTICS', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Fast LFR.', 2, 1, 'Nitro Navigator (LFR)', '69f9043e5c7cd75eb85bddf2',
        'https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/event-posters/robotics-main', 200.00, NULL,
        'ON_SPOT', 'MSIT Campus', b'1', b'1'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'ROBOTICS', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Bot carrom.', 2, 1, 'Counter Strike (Carrom)', '69f9043e5c7cd75eb85bddf2',
        'https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/event-posters/robotics-main', 200.00, NULL,
        'ON_SPOT', 'MSIT Campus', b'1', b'1');


-- INSERT initial data into the event_combos and event_combo_mappings tables

INSERT paridhi_dev_db.event_combos(event_combo_id, created_at, created_by, updated_at, updated_by, domain,
                                   event_combo_description, event_combo_name, event_combo_poster_file_id,
                                   event_combo_poster_url, event_combo_registration_fee,
                                   is_event_combo_registration_open)
VALUES (1, '2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'GAMING',
        'Combo of BGMI and Valorant.', 'Battle Royale Combo', '69f909245c7cd75eb8895fd4',
        'https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/event-posters/gaming-bgmi-valorant-combo',
        250.00, b'1'),
       (2, '2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'CODING',
        'Combo of Problematrix and CipherSiege.', 'Coding Combo', '69f90a745c7cd75eb894418c',
        'https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/event-posters/coding-problematrix-and-ciphersiege-combo',
        150.00, b'1'),
       (3, '2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'ROBOTICS', 'Combo of Armageddon
and Dribble Drive.', 'Robotics Combo', '69f909eb5c7cd75eb88ff08b',
        'https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/event-posters/robotics-armageddon-and-dribble_drive-combo',
        350.00, b'1'),
       (4, '2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'GAMING',
        'Combo of E-Football and EA FC.', 'Football Combo', '69f90adf5c7cd75eb899896b',
        'https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/event-posters/gaming-pes-and-eafc-combo',
        250.00, b'1'),
       (5, '2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'GENERAL',
        'Combo of Carrom and Chess.', 'Board Game Combo', '69f90c855c7cd75eb8af3f75',
        'https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/event-posters/general-chess-and-carrom-combo',
        80.00, b'1'),
       (6, '2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'CIVIL',
        'Combo of Setu Bandhan and TOT.', 'Civil Combo', '69f90cda5c7cd75eb8b0d3b7',
        'https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/event-posters/civil-setu-bandhan-and-tot-combo',
        120.00, b'1'),
       (7, '2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'ELECTRICAL',
        'Combo of Circuitron and Circuit Byte.', 'Electrical Combo', '69f90d365c7cd75eb8b2ff1a',
        'https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/event-posters/electrical-circuitron-and-circuit_byte-combo',
        120.00, b'1'),
       (8, '2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'ROBOTICS',
        'Combo of Shadow Tracker and Nitro Navigator.', 'LFR Combo', '69f90d7b5c7cd75eb8b49bbd',
        'https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/event-posters/robotics-shadow_tracker-and-nitro_navigator-combo',
        120.00, b'1');

INSERT paridhi_dev_db.event_combo_mappings(event_combo_id, event_id)
VALUES (1, 1),
       (1, 3),
       (2, 14),
       (2, 15),
       (3, 21),
       (3, 23),
       (4, 2),
       (4, 5),
       (5, 6),
       (5, 8),
       (6, 10),
       (6, 11),
       (7, 19),
       (7, 20),
       (8, 24),
       (8, 26);