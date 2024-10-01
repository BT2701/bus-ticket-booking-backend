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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table booking.bookings: ~1 rows (approximately)
INSERT INTO `bookings` (`id`, `customer`, `schedule`, `seatnum`, `time`, `status`) VALUES
	(1, 1, 1, '12', '2024-09-15 14:06:03', 1);

-- Dumping structure for table booking.buses
CREATE TABLE IF NOT EXISTS `buses` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `busnumber` varchar(255) DEFAULT NULL,
  `seatcount` int(11) NOT NULL,
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
INSERT INTO `buses` (`id`, `busnumber`, `seatcount`, `driver`, `img`, `category`) VALUES
	(1, '78E104783', 34, 1, NULL, 3),
	(2, '78E177777', 26, 1, NULL, 1);

-- Dumping structure for table booking.categories
CREATE TABLE IF NOT EXISTS `categories` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `seat_count` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table booking.categories: ~2 rows (approximately)
INSERT INTO `categories` (`id`, `name`, `seat_count`) VALUES
	(1, 'Xe giường nằm cao cấp', 24),
	(2, 'Xe giường nằm thông dụng', 32),
	(3, 'Xe ghế ngồi cao cấp', 36);

-- Dumping structure for table booking.customers
CREATE TABLE IF NOT EXISTS `customers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `address` varchar(255) NOT NULL DEFAULT '',
  `password` varchar(255) DEFAULT NULL,
  `birth` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table booking.customers: ~1 rows (approximately)
INSERT INTO `customers` (`id`, `name`, `phone`, `email`, `address`, `password`, `birth`) VALUES
	(1, 'Pham Van Du', '115', 'abc@gmail.com', 'New Youth', '', NULL);

-- Dumping structure for table booking.drivers
CREATE TABLE IF NOT EXISTS `drivers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `license` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `img` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table booking.drivers: ~0 rows (approximately)
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table booking.routes: ~0 rows (approximately)
INSERT INTO `routes` (`id`, `from`, `to`, `distance`, `duration`) VALUES
	(1, 1, 2, 540, '10:00:00');

-- Dumping structure for table booking.schedules
CREATE TABLE IF NOT EXISTS `schedules` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bus` int(11) NOT NULL,
  `route` int(11) NOT NULL,
  `departure` timestamp NOT NULL DEFAULT current_timestamp(),
  `arrival` timestamp NOT NULL DEFAULT current_timestamp(),
  `price` double NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `FK_schedules_buses` (`bus`) USING BTREE,
  KEY `FK_schedules_routes` (`route`) USING BTREE,
  CONSTRAINT `FK_schedules_buses` FOREIGN KEY (`bus`) REFERENCES `buses` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_schedules_routes` FOREIGN KEY (`route`) REFERENCES `routes` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table booking.schedules: ~2 rows (approximately)
INSERT INTO `schedules` (`id`, `bus`, `route`, `departure`, `arrival`, `price`) VALUES
	(1, 2, 1, '2024-09-15 12:00:00', '2024-09-14 22:00:00', 320000),
	(2, 1, 1, '2024-09-17 09:23:14', '2024-09-17 09:23:15', 280000);

-- Dumping structure for table booking.stations
CREATE TABLE IF NOT EXISTS `stations` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table booking.stations: ~2 rows (approximately)
INSERT INTO `stations` (`id`, `name`, `address`) VALUES
	(1, 'BX Miền Đông', 'TP HCM'),
	(2, 'Ga Tuy Hòa', 'Phú Yên');

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table booking.tokens: ~0 rows (approximately)

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
