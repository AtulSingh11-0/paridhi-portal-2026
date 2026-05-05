SET FOREIGN_KEY_CHECKS = 0;

--
-- Table structure for table `blacklisted_tokens`
--
CREATE TABLE `blacklisted_tokens`
(
    `token_id`   bigint        NOT NULL AUTO_INCREMENT,
    `created_at` datetime(6)   NOT NULL,
    `created_by` varchar(255) DEFAULT NULL,
    `updated_at` datetime(6)   NOT NULL,
    `updated_by` varchar(255) DEFAULT NULL,
    `expires_at` datetime(6)   NOT NULL,
    `token`      varchar(1000) NOT NULL,
    PRIMARY KEY (`token_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Table structure for table `event_combo_mappings`
--
CREATE TABLE `event_combo_mappings`
(
    `event_combo_id` bigint NOT NULL,
    `event_id`       bigint NOT NULL,
    PRIMARY KEY (`event_combo_id`, `event_id`),
    KEY `FKa37es4tokg7x8vagwcurc5m5n` (`event_id`),
    CONSTRAINT `FKa37es4tokg7x8vagwcurc5m5n` FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`),
    CONSTRAINT `FKhckd3p3cnw6i4oqf9ooqu1ul1` FOREIGN KEY (`event_combo_id`) REFERENCES `event_combos` (`event_combo_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Table structure for table `event_combos`
--
CREATE TABLE `event_combos`
(
    `event_combo_id`                   bigint                                                                         NOT NULL AUTO_INCREMENT,
    `created_at`                       datetime(6)                                                                    NOT NULL,
    `created_by`                       varchar(255) DEFAULT NULL,
    `updated_at`                       datetime(6)                                                                    NOT NULL,
    `updated_by`                       varchar(255) DEFAULT NULL,
    `domain`                           enum ('CIVIL','CODING','ELECTRICAL','EXCLUSIVE','GAMING','GENERAL','ROBOTICS') NOT NULL,
    `event_combo_description`          varchar(2000)                                                                  NOT NULL,
    `event_combo_name`                 varchar(255)                                                                   NOT NULL,
    `event_combo_poster_file_id`       varchar(255)                                                                   NOT NULL,
    `event_combo_poster_url`           varchar(255)                                                                   NOT NULL,
    `event_combo_registration_fee`     decimal(38, 2)                                                                 NOT NULL,
    `is_event_combo_registration_open` bit(1)                                                                         NOT NULL,
    PRIMARY KEY (`event_combo_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Table structure for table `events`
--
CREATE TABLE `events`
(
    `event_id`                   bigint                                                                         NOT NULL AUTO_INCREMENT,
    `created_at`                 datetime(6)                                                                    NOT NULL,
    `created_by`                 varchar(255) DEFAULT NULL,
    `updated_at`                 datetime(6)                                                                    NOT NULL,
    `updated_by`                 varchar(255) DEFAULT NULL,
    `domain`                     enum ('CIVIL','CODING','ELECTRICAL','EXCLUSIVE','GAMING','GENERAL','ROBOTICS') NOT NULL,
    `event_coordinator_details`  json                                                                           NOT NULL,
    `event_date`                 datetime(6)                                                                    NOT NULL,
    `event_description`          varchar(2000)                                                                  NOT NULL,
    `event_max_players`          int                                                                            NOT NULL,
    `event_min_players`          int                                                                            NOT NULL,
    `event_name`                 varchar(255)                                                                   NOT NULL,
    `event_poster_file_id`       varchar(255)                                                                   NOT NULL,
    `event_poster_url`           varchar(255)                                                                   NOT NULL,
    `event_registration_fee`     decimal(38, 2)                                                                 NOT NULL,
    `event_rule_book_url`        varchar(255) DEFAULT NULL,
    `event_type`                 enum ('MAIN','ON_SPOT')                                                        NOT NULL,
    `event_venue`                varchar(255)                                                                   NOT NULL,
    `is_event_registration_open` bit(1)                                                                         NOT NULL,
    `is_team_event`              bit(1)                                                                         NOT NULL,
    PRIMARY KEY (`event_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Table structure for table `mrd_registrations`
--
CREATE TABLE `mrd_registrations`
(
    `mrd_id`       bigint                                                                                                                                                  NOT NULL AUTO_INCREMENT,
    `created_at`   datetime(6)                                                                                                                                             NOT NULL,
    `created_by`   varchar(255) DEFAULT NULL,
    `updated_at`   datetime(6)                                                                                                                                             NOT NULL,
    `updated_by`   varchar(255) DEFAULT NULL,
    `college`      varchar(255)                                                                                                                                            NOT NULL,
    `department`   enum ('AIDS','AIML','BA','BBA','BCA','BCOM','BSC','CE','CSBS','CSE','CYS','DS','ECE','EE','IT','LLB','LLM','MA','MBA','MCA','MCOM','ME','MSC','OTHERS') NOT NULL,
    `email`        varchar(255)                                                                                                                                            NOT NULL,
    `gid`          varchar(255)                                                                                                                                            NOT NULL,
    `has_paid`     bit(1)                                                                                                                                                  NOT NULL,
    `name`         varchar(255)                                                                                                                                            NOT NULL,
    `phone_number` varchar(15)                                                                                                                                             NOT NULL,
    `roll_no`      varchar(255) DEFAULT NULL,
    `year`         enum ('ALUMNI','FIRST','FOURTH','NOT_APPLICABLE','OTHER','OTHERS','PASSOUT','SECOND','THIRD')                                                           NOT NULL,
    `user_id`      bigint                                                                                                                                                  NOT NULL,
    PRIMARY KEY (`mrd_id`),
    UNIQUE KEY `UKpe4j5gu45nametct5w1nxugrx` (`gid`),
    KEY `FK269c5i9w62dm16e280u2lbtdg` (`user_id`),
    CONSTRAINT `FK269c5i9w62dm16e280u2lbtdg` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Table structure for table `otp_tokens`
--
CREATE TABLE `otp_tokens`
(
    `otp_id`     bigint       NOT NULL AUTO_INCREMENT,
    `created_at` datetime(6)  NOT NULL,
    `created_by` varchar(255) DEFAULT NULL,
    `updated_at` datetime(6)  NOT NULL,
    `updated_by` varchar(255) DEFAULT NULL,
    `attempts`   int          NOT NULL,
    `email`      varchar(255) NOT NULL,
    `expires_at` datetime(6)  NOT NULL,
    `is_used`    bit(1)       NOT NULL,
    `otp`        varchar(6)   NOT NULL,
    `user_id`    bigint       NOT NULL,
    PRIMARY KEY (`otp_id`),
    KEY `FKjyk2kyj8ul2bjqjpentlrpyi1` (`user_id`),
    CONSTRAINT `FKjyk2kyj8ul2bjqjpentlrpyi1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Table structure for table `password_reset_tokens`
--
CREATE TABLE `password_reset_tokens`
(
    `token_id`    bigint      NOT NULL AUTO_INCREMENT,
    `created_at`  datetime(6) NOT NULL,
    `created_by`  varchar(255) DEFAULT NULL,
    `updated_at`  datetime(6) NOT NULL,
    `updated_by`  varchar(255) DEFAULT NULL,
    `attempts`    int         NOT NULL,
    `expiry_date` datetime(6) NOT NULL,
    `is_used`     bit(1)      NOT NULL,
    `token`       varchar(6)  NOT NULL,
    `user_id`     bigint      NOT NULL,
    PRIMARY KEY (`token_id`),
    UNIQUE KEY `UK71lqwbwtklmljk3qlsugr1mig` (`token`),
    KEY `FKk3ndxg5xp6v7wd4gjyusp15gq` (`user_id`),
    CONSTRAINT `FKk3ndxg5xp6v7wd4gjyusp15gq` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Table structure for table `team_contacts`
--
CREATE TABLE `team_contacts`
(
    `team_id`      bigint NOT NULL,
    `name`         varchar(255) DEFAULT NULL,
    `phone_number` varchar(255) DEFAULT NULL,
    KEY `FKmjt94w1dma7u748bo2wmnehg0` (`team_id`),
    CONSTRAINT `FKmjt94w1dma7u748bo2wmnehg0` FOREIGN KEY (`team_id`) REFERENCES `teams` (`team_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Table structure for table `team_gids`
--
CREATE TABLE `team_gids`
(
    `team_id` bigint NOT NULL,
    `gids`    varchar(255) DEFAULT NULL,
    KEY `FKsv57brcmp861x5dgiosvtydm1` (`team_id`),
    CONSTRAINT `FKsv57brcmp861x5dgiosvtydm1` FOREIGN KEY (`team_id`) REFERENCES `teams` (`team_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Table structure for table `teams`
--
CREATE TABLE `teams`
(
    `team_id`       bigint       NOT NULL AUTO_INCREMENT,
    `created_at`    datetime(6)  NOT NULL,
    `created_by`    varchar(255)                           DEFAULT NULL,
    `updated_at`    datetime(6)  NOT NULL,
    `updated_by`    varchar(255)                           DEFAULT NULL,
    `has_paid`      bit(1)       NOT NULL,
    `has_played`    bit(1)       NOT NULL,
    `has_qualified` bit(1)       NOT NULL,
    `position`      enum ('FIRST','NONE','SECOND','THIRD') DEFAULT NULL,
    `team_name`     varchar(255) NOT NULL,
    `tid`           varchar(255) NOT NULL,
    `event_id`      bigint       NOT NULL,
    PRIMARY KEY (`team_id`),
    UNIQUE KEY `UK7v82j9re5gwg699c6hf5l8wxj` (`tid`),
    KEY `FKsjcq76oa92bm6gjbebp2spajm` (`event_id`),
    CONSTRAINT `FKsjcq76oa92bm6gjbebp2spajm` FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Table structure for table `users`
--
CREATE TABLE `users`
(
    `user_id`             bigint                                            NOT NULL AUTO_INCREMENT,
    `created_at`          datetime(6)                                       NOT NULL,
    `created_by`          varchar(255)                                                                                                                                            DEFAULT NULL,
    `updated_at`          datetime(6)                                       NOT NULL,
    `updated_by`          varchar(255)                                                                                                                                            DEFAULT NULL,
    `college`             varchar(255)                                                                                                                                            DEFAULT NULL,
    `department`          enum ('AIDS','AIML','BA','BBA','BCA','BCOM','BSC','CE','CSBS','CSE','CYS','DS','ECE','EE','IT','LLB','LLM','MA','MBA','MCA','MCOM','ME','MSC','OTHERS') DEFAULT NULL,
    `email`               varchar(255)                                      NOT NULL,
    `is_logged_in`        bit(1)                                            NOT NULL,
    `is_profile_created`  bit(1)                                            NOT NULL,
    `is_verified`         bit(1)                                            NOT NULL,
    `last_login`          datetime(6)                                                                                                                                             DEFAULT NULL,
    `name`                varchar(255)                                      NOT NULL,
    `password`            varchar(64)                                       NOT NULL,
    `phone_number`        varchar(15)                                                                                                                                             DEFAULT NULL,
    `profile_picture_url` varchar(255)                                                                                                                                            DEFAULT NULL,
    `role`                enum ('ROLE_ADMIN','ROLE_SUPERADMIN','ROLE_USER') NOT NULL,
    `roll_no`             varchar(255)                                                                                                                                            DEFAULT NULL,
    `year`                enum ('ALUMNI','FIRST','FOURTH','NOT_APPLICABLE','OTHER','OTHERS','PASSOUT','SECOND','THIRD')                                                           DEFAULT NULL,
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4


  COLLATE = utf8mb4_0900_ai_ci;

SET FOREIGN_KEY_CHECKS = 1;

