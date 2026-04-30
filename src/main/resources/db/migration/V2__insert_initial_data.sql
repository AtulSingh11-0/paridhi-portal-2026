-- INSERT initial data into the events table

INSERT INTO paridhi_dev_db.events (created_at, created_by, updated_at, updated_by, domain, event_coordinator_details,
                                   event_date, event_description, event_max_players, event_min_players, event_name,
                                   event_poster_file_id, event_poster_url, event_registration_fee, event_rule_book_url,
                                   event_type, event_venue, is_event_registration_open, is_team_event)

VALUES ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'GAMING', '[
  "Aditya Kumar - 9823456789",
  "Priya Sharma - 9812365478"
]', '2026-05-15 10:00:00.000000', 'Squad battle royale.', 5, 4, 'BGMI', 'file_2',
        'https://images.unsplash.com/photo-1511512578047-dfb367046420?q=80&w=1471&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
        150.00, NULL, 'MAIN', 'MSIT Campus', b'0', b'1'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'GAMING', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Football esports.', 1, 1, 'E-Football (PES)', 'file_3',
        'https://images.unsplash.com/photo-1593305841991-05c297ba4575?q=80&w=1357&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
        150.00, NULL, 'MAIN', 'MSIT Campus', b'0', b'0'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'GAMING', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Tactical shooter.', 5, 5, 'Valorant', 'file_4',
        'https://plus.unsplash.com/premium_photo-1677870728119-52aef052d7ef?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
        150.00, NULL, 'MAIN', 'MSIT Campus', b'0', b'1'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'GAMING', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Fighting matches.', 1, 1, 'Mortal Kombat', 'file_5',
        'https://plus.unsplash.com/premium_photo-1674374443275-20dae04975ac?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
        150.00, NULL, 'ON_SPOT', 'MSIT Campus', b'0', b'0'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'GAMING', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Football knockout.', 1, 1, 'EA FC 25', 'file_6',
        'https://images.unsplash.com/photo-1612287230202-1ff1d85d1bdf?q=80&w=1471&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
        150.00, NULL, 'ON_SPOT', 'MSIT Campus', b'0', b'0'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'GENERAL', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Carrom board game.', 1, 1, 'Carrom', 'file_7',
        'https://images.unsplash.com/photo-1652558973276-7c57960f50cd?q=80&w=1074&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
        50.00, NULL, 'MAIN', 'MSIT Campus', b'0', b'0'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'GENERAL', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Quiz event.', 2, 1, 'Binge Quiz', 'file_8',
        'https://plus.unsplash.com/premium_photo-1664910122062-a31e337aa79a?q=80&w=870&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
        50.00, NULL, 'MAIN', 'MSIT Campus', b'0', b'1'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'GENERAL', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Chess battles.', 1, 1, 'Chess', 'file_9',
        'https://images.unsplash.com/photo-1628867578021-c2eaad2ce46c?q=80&w=870&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
        50.00, NULL, 'ON_SPOT', 'MSIT Campus', b'0', b'0'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'GENERAL', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Dart accuracy.', 1, 1, 'Dart', 'file_10',
        'https://images.unsplash.com/photo-1579019163248-e7761241d85a?q=80&w=870&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
        50.00, NULL, 'ON_SPOT', 'MSIT Campus', b'0', b'0'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'CIVIL', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Bridge building challenge.', 3, 2, 'Setu Bandhan', 'file_11',
        'https://plus.unsplash.com/premium_photo-1664474927853-900d5ee1fd80?q=80&w=870&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
        80.00, NULL, 'MAIN', 'MSIT Campus', b'0', b'1'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'CIVIL', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Tower building event.', 3, 2, 'TOT', 'file_12',
        'https://images.unsplash.com/photo-1684497404598-6e844dff9cde?q=80&w=869&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
        80.00, NULL, 'MAIN', 'MSIT Campus', b'0', b'1'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'CIVIL', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Architectural design.', 2, 1, 'MegaArch', 'file_13',
        'https://images.unsplash.com/photo-1556894769-b9a5dab851c0?q=80&w=870&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
        80.00, NULL, 'MAIN', 'MSIT Campus', b'0', b'1'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'CIVIL', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'CAD competition.', 1, 1, 'CAD Clash', 'file_14',
        'https://images.unsplash.com/photo-1503708928676-1cb796a0891e?q=80&w=1074&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
        80.00, NULL, 'MAIN', 'MSIT Campus', b'0', b'0'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'CODING', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Algorithm contest.', 1, 1, 'Problematrx', 'file_15',
        'https://images.unsplash.com/photo-1461749280684-dccba630e2f6?q=80&w=1469&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
        100.00, NULL, 'MAIN', 'MSIT Campus', b'0', b'0'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'CODING', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Security coding.', 1, 1, 'CipherSiege', 'file_16',
        'https://plus.unsplash.com/premium_photo-1661877737564-3dfd7282efcb?q=80&w=900&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
        100.00, NULL, 'MAIN', 'MSIT Campus', b'0', b'0'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'CODING', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Debugging contest.', 1, 1, 'DéjàView', 'file_17',
        'https://images.unsplash.com/photo-1542831371-29b0f74f9713?q=80&w=870&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
        100.00, NULL, 'MAIN', 'MSIT Campus', b'0', b'0'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'CODING', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Quick coding.', 1, 1, 'Flash Code', 'file_18',
        'https://images.unsplash.com/photo-1607706189992-eae578626c86?q=80&w=870&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
        100.00, NULL, 'ON_SPOT', 'MSIT Campus', b'0', b'0'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'CODING', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'StackOverflow style.', 1, 1, 'HeapOverflow', 'file_19',
        'https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?q=80&w=870&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
        100.00, NULL, 'ON_SPOT', 'MSIT Campus', b'0', b'0'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'ELECTRICAL', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Circuit design.', 2, 1, 'Circuitron', 'file_20',
        'https://pornmz.com/wp-content/uploads/2024/05/Brazzers-Bridgette-B-Moriah-Mills-Wedding-Surprise-Pornmz.jpg',
        80.00, NULL, 'MAIN', 'MSIT Campus', b'0', b'1'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'ELECTRICAL', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Quick circuits.', 1, 1, 'Circuit Byte', 'file_21',
        'https://i0.wp.com/namethatpornad.com/wp-content/uploads/2024/08/Kimmy-Granger-Jayden-Jaymes-in-Best-Of-ZZ-Johnny-Sins-Brazzers-upcoming.jpg?w=750&ssl=1',
        80.00, NULL, 'ON_SPOT', 'MSIT Campus', b'0', b'0'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'ROBOTICS', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Robot war.', 4, 2, 'Armageddon (War)', 'file_22',
        'https://fapptime.com/wp-content/uploads/2025/07/Comatozze-Step-Sister-got-Caught-Sending-Nudes-Video-Leaked-FappTime.com.jpeg',
        200.00, NULL, 'MAIN', 'MSIT Campus', b'0', b'1'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'ROBOTICS', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Robot race.', 3, 2, 'Dare to Desiny (Race)', 'file_23',
        'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRHYIUFxnO3keKTGiWmZM6Kf6_vm9w_9P7kfg&s', 200.00, NULL,
        'MAIN', 'MSIT Campus', b'0', b'1'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'ROBOTICS', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Robot soccer.', 4, 3, 'Dribble Drive (Soccer)', 'file_24',
        'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTCc7nCraXnj5QFAPKBlz8I-Y8rUX2H5fXZmg&s', 200.00, NULL,
        'MAIN', 'MSIT Campus', b'0', b'1'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'ROBOTICS', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Line follower.', 2, 1, 'Shadow Tracker (LFR)', 'file_25',
        'https://pix-egi.phncdn.com/c6251/videos/202509/25/23942875/original/01998243-c859-7f7b-9481-4242e2d6b15d.jpg/plain/rs:fit:1280:720?validfrom=1751342400&validto=4891363200&hash=lZLaRdG5pLaNyvVrdRpnU2cMQRA%3D',
        200.00, NULL, 'MAIN', 'MSIT Campus', b'0', b'1'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'ROBOTICS', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', '1v1 race.', 2, 2, 'TwinTurbo (Race 1v1)', 'file_26',
        'https://assets.rabbitsreviews.com/videos/20251230-2-11450397-5317-5317-001.jpg', 200.00, NULL, 'ON_SPOT',
        'MSIT Campus', b'0', b'1'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'ROBOTICS', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Fast LFR.', 2, 1, 'Nitro Navigator (LFR)', 'file_27',
        'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR3hQtSYOfRsr3sMnEeEpyMR0nBuARPo4p1Gg&s', 200.00, NULL,
        'ON_SPOT', 'MSIT Campus', b'0', b'1'),

       ('2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'ROBOTICS', '[
         "Aditya Kumar - 9823456789",
         "Priya Sharma - 9812365478"
       ]', '2026-05-15 10:00:00.000000', 'Bot carrom.', 2, 1, 'Counter Strike (Carrom)', 'file_28',
        'https://www.gosexpod.com/thumbs-480x270-12/224529.jpg', 200.00, NULL, 'ON_SPOT', 'MSIT Campus', b'0', b'1');


-- INSERT initial data into the event_combos and event_combo_mappings tables

INSERT paridhi_dev_db.event_combos(event_combo_id, created_at, created_by, updated_at, updated_by, domain,
                                   event_combo_description, event_combo_name, event_combo_poster_file_id,
                                   event_combo_poster_url, event_combo_registration_fee,
                                   is_event_combo_registration_open)
VALUES (1, '2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'GAMING',
        'Combo of BGMI and Valorant.', 'Battle Royale Combo', 'file_29',
        'https://ei.phncdn.com/videos/202410/18/459300741/original/(m=q8L8KXZbeaSaaTbaAaaaa)(mh=HIozM5pmiYYzwTGp)0.jpg',
        250.00, b'0'),
       (2, '2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'CODING', 'Combo of Problematrx
and CipherSiege.', 'Coding Combo', 'file_30',
        'https://pix-egi.phncdn.com/c6251/videos/202508/27/20673385/original/0198ef4c-b8bc-787d-bd37-011709b00896.png/plain/rs:fit:1280:720?validfrom=1751342400&validto=4891363200&hash=4b3ZKXaJyN4JUClM5XfW5iBXhlM%3D',
        150.00, b'0'),
       (3, '2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'ROBOTICS', 'Combo of Armageddon
and Dribble Drive.', 'Robotics Combo', 'file_31',
        'https://thumb-cdn77.xvideos-cdn.com/4a9a839e-2183-4196-a72b-2b06ea1e5b94/0/xv_25_p.jpg',
        350.00, b'0'),
       (4, '2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'GAMING',
        'Combo of E-Football and EA FC.', 'Football Combo', 'file_32',
        'https://tn.pornhits.com/media/tn/585463_1.webp',
        250.00, b'0'),
       (5, '2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'GENERAL',
        'Combo of Carrom and Chess.', 'Board Game Combo', 'file_33',
        'https://ei.phncdn.com/videos/202404/29/451772431/original/(m=qZ_5HJZbeaSaaTbaAaaaa)(mh=THOWaOlGpLygxmm1)0.jpg',
        80.00, b'0'),
       (6, '2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'CIVIL',
        'Combo of Setu Bandhan and TOT.', 'Civil Combo', 'file_34',
        'https://pix-egi.phncdn.com/c6251/videos/202510/13/26108565/original/0199de2f-f568-7ad9-83bd-0db57d856b87.png/plain/rs:fit:1280:720?validfrom=1751342400&validto=4891363200&hash=7OMrjDzLaoyGSmJ5hRlrlGo4TU8%3D',
        120.00, b'0'),
       (7, '2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'ELECTRICAL',
        'Combo of Circuitron and Circuit Byte.', 'Electrical Combo', 'file_35',
        'https://ei.phncdn.com/videos/202310/20/441532911/original/(m=q1Y5LZYbeaAaGwObaaaa)(mh=nGtrQQMO8p0ySw7y)0.jpg',
        120.00, b'0'),
       (8, '2026-05-15 10:00:00.000000', 'system', '2026-05-15 10:00:00.000000', 'system', 'ROBOTICS',
        'Combo of Shadow Tracker and Nitro Navigator.', 'LFR Combo', 'file_36',
        'https://imghd.cdnvidhd.com/thumbs/8364/thumb1.1.webp?',
        120.00, b'0');

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