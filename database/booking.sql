-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               10.4.27-MariaDB - mariadb.org binary distribution
-- Server OS:                    Win64
-- HeidiSQL Version:             12.3.0.6589
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for booking
CREATE DATABASE IF NOT EXISTS `booking` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;
USE `booking`;

-- Dumping structure for table booking.bookings
CREATE TABLE IF NOT EXISTS `bookings` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `customer` int(11) NOT NULL,
  `schedule` int(11) NOT NULL,
  `seatnum` varchar(255) NOT NULL,
  `time` timestamp NOT NULL DEFAULT current_timestamp(),
  `status` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_bookings_customers` (`customer`) USING BTREE,
  KEY `FK_bookings_schedules` (`schedule`) USING BTREE,
  CONSTRAINT `FK_bookings_customers` FOREIGN KEY (`customer`) REFERENCES `customers` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_bookings_schedules` FOREIGN KEY (`schedule`) REFERENCES `schedules` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table booking.bookings: ~17 rows (approximately)
INSERT INTO `bookings` (`id`, `customer`, `schedule`, `seatnum`, `time`, `status`) VALUES
	(1, 1, 1, 'A12', '2024-09-15 14:06:03', 1),
	(3, 2, 2, 'A09', '2024-10-05 05:56:31', 1),
	(4, 2, 1, 'B12', '2024-10-09 06:55:10', 0),
	(5, 2, 1, 'A07', '2024-10-10 16:53:10', 1),
	(6, 2, 1, 'A06', '2024-10-10 16:53:10', 1),
	(7, 2, 1, 'A03', '2024-10-10 16:53:10', 1),
	(8, 2, 1, 'A10', '2024-10-10 17:03:55', 1),
	(9, 2, 1, 'A11', '2024-10-10 17:03:55', 1),
	(10, 2, 1, 'A09', '2024-10-10 17:03:55', 1),
	(11, 3, 1, 'A05', '2024-10-12 06:12:37', 1),
	(12, 3, 1, 'A04', '2024-10-12 06:12:37', 1),
	(13, 2, 3, 'A01', '2024-10-13 12:06:45', 0),
	(14, 1, 4, 'B01', '2024-10-13 12:07:01', 0),
	(15, 2, 5, 'B02', '2024-10-13 12:07:14', 0),
	(16, 1, 2, 'A07', '2024-10-16 04:39:22', 1),
	(17, 1, 3, 'A06', '2024-10-16 04:45:19', 1),
	(18, 1, 2, 'A01', '2024-10-16 04:46:09', 1),
	(19, 1, 2, 'A10', '2024-10-16 04:47:15', 1),
	(20, 1, 1, 'A01', '2024-10-16 04:48:33', 1);

-- Dumping structure for table booking.buses
CREATE TABLE IF NOT EXISTS `buses` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `busnumber` varchar(255) DEFAULT NULL,
  `driver` int(11) NOT NULL,
  `img` varchar(255) DEFAULT NULL,
  `category` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_buses_drivers` (`driver`) USING BTREE,
  KEY `FKayk758lwwf4yofewrsyprt3og` (`category`),
  CONSTRAINT `FK_buses_drivers` FOREIGN KEY (`driver`) REFERENCES `drivers` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FKayk758lwwf4yofewrsyprt3og` FOREIGN KEY (`category`) REFERENCES `categories` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table booking.buses: ~2 rows (approximately)
INSERT INTO `buses` (`id`, `busnumber`, `driver`, `img`, `category`) VALUES
	(1, '78E104783', 1, NULL, 2),
	(2, '78E177777', 1, NULL, 1);

-- Dumping structure for table booking.categories
CREATE TABLE IF NOT EXISTS `categories` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `seat_count` int(11) NOT NULL,
  `price` double NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table booking.categories: ~3 rows (approximately)
INSERT INTO `categories` (`id`, `name`, `seat_count`, `price`) VALUES
	(1, 'Xe giường nằm cao cấp', 24, 450000),
	(2, 'Xe giường nằm thông dụng', 32, 320000),
	(3, 'Xe ghế ngồi cao cấp', 36, 280000);

-- Dumping structure for table booking.contact_us
CREATE TABLE IF NOT EXISTS `contact_us` (
  `sender` varchar(255) NOT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(100) NOT NULL,
  `phone` varchar(10) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `content` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table booking.contact_us: ~0 rows (approximately)

-- Dumping structure for table booking.customers
CREATE TABLE IF NOT EXISTS `customers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT '',
  `password` varchar(255) DEFAULT NULL,
  `birth` date DEFAULT NULL,
  `role` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKcqlj56bjy1fpy4ruk8n4vgwm6` (`role`),
  CONSTRAINT `FKcqlj56bjy1fpy4ruk8n4vgwm6` FOREIGN KEY (`role`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table booking.customers: ~2 rows (approximately)
INSERT INTO `customers` (`id`, `name`, `phone`, `email`, `address`, `password`, `birth`, `role`) VALUES
	(1, 'Pham Van Du', '115', 'abc@gmail.com', 'New Youth', '', NULL, NULL),
	(2, 'Trưởng Dương Thành', '0386094783', 'dttruonga8tqtpy@gmail.com', 'abc', '$2a$10$9SxZOYcy4e38VmnBiuYD4Ok/ahwJRIIqwxhoOAChx3Xc95PWGFVuq', '2003-10-23', NULL),
	(3, '', '', '', NULL, NULL, NULL, NULL);

-- Dumping structure for table booking.drivers
CREATE TABLE IF NOT EXISTS `drivers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `license` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `img` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table booking.drivers: ~1 rows (approximately)
INSERT INTO `drivers` (`id`, `name`, `license`, `phone`, `img`) VALUES
	(1, 'Duong Thanh Truong', '3121410546', '0386094783', NULL);

-- Dumping structure for table booking.ewalletpay
CREATE TABLE IF NOT EXISTS `ewalletpay` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `payment` int(11) NOT NULL,
  `provider` varchar(255) DEFAULT NULL,
  `transactionid` int(11) NOT NULL,
  `status` int(11) NOT NULL,
  `time` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `FK_ewalletpay_ewalletprovider` (`provider`),
  KEY `FK_ewalletpay_payments` (`payment`) USING BTREE,
  CONSTRAINT `FK_ewalletpay_payments` FOREIGN KEY (`payment`) REFERENCES `payments` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table booking.ewalletpay: ~1 rows (approximately)
INSERT INTO `ewalletpay` (`id`, `payment`, `provider`, `transactionid`, `status`, `time`) VALUES
	(1, 1, 'Momo', 1111, 0, '2024-09-15 14:11:18');

-- Dumping structure for table booking.feedback
CREATE TABLE IF NOT EXISTS `feedback` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `content` varchar(255) DEFAULT NULL,
  `rating` int(11) DEFAULT NULL CHECK (`rating` between 1 and 5),
  `date` timestamp NOT NULL DEFAULT current_timestamp(),
  `booking` int(11) DEFAULT NULL,
  `customer` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6so6ck10mf3iqi3pnjgmkrc0j` (`booking`),
  KEY `FKajio2t3soy2avncn4dwt07p6` (`customer`),
  CONSTRAINT `FK6so6ck10mf3iqi3pnjgmkrc0j` FOREIGN KEY (`booking`) REFERENCES `bookings` (`id`),
  CONSTRAINT `FKajio2t3soy2avncn4dwt07p6` FOREIGN KEY (`customer`) REFERENCES `customers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table booking.feedback: ~0 rows (approximately)

-- Dumping structure for table booking.forgotpassword
CREATE TABLE IF NOT EXISTS `forgotpassword` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `reset_expiration` datetime(6) DEFAULT NULL,
  `reset_token` varchar(255) DEFAULT NULL,
  `customer` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKjg2ah7kseur4h54sis2t5nm17` (`customer`),
  CONSTRAINT `FKfom2ha5hapnv1ynraj67x66gu` FOREIGN KEY (`customer`) REFERENCES `customers` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table booking.forgotpassword: ~0 rows (approximately)

-- Dumping structure for table booking.notifications
CREATE TABLE IF NOT EXISTS `notifications` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  `date_sent` timestamp NOT NULL DEFAULT current_timestamp(),
  `read_at` timestamp NULL DEFAULT NULL,
  `customer` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK61e5552050ot2nhk5rwt6g93k` (`customer`),
  CONSTRAINT `FK61e5552050ot2nhk5rwt6g93k` FOREIGN KEY (`customer`) REFERENCES `customers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table booking.notifications: ~0 rows (approximately)

-- Dumping structure for table booking.payments
CREATE TABLE IF NOT EXISTS `payments` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `booking` int(11) NOT NULL,
  `amount` double NOT NULL DEFAULT 0,
  `method` varchar(255) DEFAULT NULL,
  `time` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `FK_payments_bookings` (`booking`) USING BTREE,
  CONSTRAINT `FK_payments_bookings` FOREIGN KEY (`booking`) REFERENCES `bookings` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table booking.payments: ~1 rows (approximately)
INSERT INTO `payments` (`id`, `booking`, `amount`, `method`, `time`) VALUES
	(1, 1, 320000, 'Ví điện tử', '2024-09-15 14:08:32');

-- Dumping structure for table booking.roles
CREATE TABLE IF NOT EXISTS `roles` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKofx66keruapi6vyqpv6f2or37` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table booking.roles: ~0 rows (approximately)

-- Dumping structure for table booking.routes
CREATE TABLE IF NOT EXISTS `routes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `from` int(11) DEFAULT NULL,
  `to` int(11) DEFAULT NULL,
  `distance` int(11) NOT NULL DEFAULT 0,
  `duration` time DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_routes_stations` (`from`),
  KEY `FK_routes_stations_2` (`to`),
  CONSTRAINT `FK_routes_stations` FOREIGN KEY (`from`) REFERENCES `stations` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_routes_stations_2` FOREIGN KEY (`to`) REFERENCES `stations` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table booking.routes: ~4 rows (approximately)
INSERT INTO `routes` (`id`, `from`, `to`, `distance`, `duration`) VALUES
	(1, 1, 2, 540, '10:00:00'),
	(2, 2, 1, 540, '10:00:00'),
	(3, 4, 5, 600, '12:00:00'),
	(4, 1, 5, 320, '08:00:00');

-- Dumping structure for table booking.schedules
CREATE TABLE IF NOT EXISTS `schedules` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bus` int(11) NOT NULL,
  `route` int(11) NOT NULL,
  `departure` timestamp NOT NULL DEFAULT current_timestamp(),
  `arrival` timestamp NOT NULL DEFAULT current_timestamp(),
  `price` double NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_schedules_buses` (`bus`) USING BTREE,
  KEY `FK_schedules_routes` (`route`) USING BTREE,
  CONSTRAINT `FK_schedules_buses` FOREIGN KEY (`bus`) REFERENCES `buses` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_schedules_routes` FOREIGN KEY (`route`) REFERENCES `routes` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table booking.schedules: ~4 rows (approximately)
INSERT INTO `schedules` (`id`, `bus`, `route`, `departure`, `arrival`, `price`) VALUES
	(1, 2, 1, '2024-10-19 17:00:00', '2024-09-14 22:00:00', 0),
	(2, 1, 1, '2024-10-19 17:00:00', '2024-09-17 09:23:15', 0),
	(3, 1, 2, '2024-10-19 17:00:00', '2024-10-13 12:04:45', 0),
	(4, 2, 3, '2024-10-13 12:05:02', '2024-10-13 12:05:03', 0),
	(5, 2, 4, '2024-10-13 12:05:12', '2024-10-13 12:05:12', 0);

-- Dumping structure for table booking.stations
CREATE TABLE IF NOT EXISTS `stations` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table booking.stations: ~4 rows (approximately)
INSERT INTO `stations` (`id`, `name`, `address`) VALUES
	(1, 'BX Miền Đông', 'TP HCM'),
	(2, 'Ga Tuy Hòa', 'Phú Yên'),
	(3, 'BX Miền Tây', 'TP HCM'),
	(4, 'BX Long Thành', 'Bình Dương'),
	(5, 'BX Thành Bưởi', 'Đà Lạt');

-- Dumping structure for table booking.tokens
CREATE TABLE IF NOT EXISTS `tokens` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `expiration_date` datetime(6) DEFAULT NULL,
  `is_access_expired` bit(1) NOT NULL,
  `refresh_expiration` datetime(6) DEFAULT NULL,
  `refresh_token` varchar(255) NOT NULL,
  `access_token` varchar(255) NOT NULL,
  `customer` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmlhw699dfdecloobup9107sh1` (`customer`),
  CONSTRAINT `FKmlhw699dfdecloobup9107sh1` FOREIGN KEY (`customer`) REFERENCES `customers` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table booking.tokens: ~0 rows (approximately)

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;