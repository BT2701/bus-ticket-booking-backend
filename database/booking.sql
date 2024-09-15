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
  `customerid` int(11) NOT NULL,
  `scheduleid` int(11) NOT NULL,
  `seatnum` int(11) NOT NULL,
  `time` timestamp NOT NULL DEFAULT current_timestamp(),
  `status` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_bookings_customers` (`customerid`),
  KEY `FK_bookings_schedules` (`scheduleid`),
  CONSTRAINT `FK_bookings_customers` FOREIGN KEY (`customerid`) REFERENCES `customers` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_bookings_schedules` FOREIGN KEY (`scheduleid`) REFERENCES `schedules` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table booking.bookings: ~1 rows (approximately)
INSERT INTO `bookings` (`id`, `customerid`, `scheduleid`, `seatnum`, `time`, `status`) VALUES
	(1, 1, 1, 12, '2024-09-15 14:06:03', 1);

-- Dumping structure for table booking.buses
CREATE TABLE IF NOT EXISTS `buses` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `busnumber` varchar(10) NOT NULL DEFAULT '',
  `seatcount` int(11) NOT NULL,
  `bustype` varchar(50) NOT NULL DEFAULT '',
  `driverid` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_buses_drivers` (`driverid`),
  CONSTRAINT `FK_buses_drivers` FOREIGN KEY (`driverid`) REFERENCES `drivers` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table booking.buses: ~1 rows (approximately)
INSERT INTO `buses` (`id`, `busnumber`, `seatcount`, `bustype`, `driverid`) VALUES
	(2, '78E177777', 26, 'Vip', 1);

-- Dumping structure for table booking.customers
CREATE TABLE IF NOT EXISTS `customers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL DEFAULT '',
  `phone` varchar(10) NOT NULL DEFAULT '',
  `email` varchar(50) NOT NULL DEFAULT '',
  `address` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table booking.customers: ~1 rows (approximately)
INSERT INTO `customers` (`id`, `name`, `phone`, `email`, `address`) VALUES
	(1, 'Pham Van Du', '115', 'abc@gmail.com', 'New Youth');

-- Dumping structure for table booking.drivers
CREATE TABLE IF NOT EXISTS `drivers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL DEFAULT '',
  `license` varchar(50) NOT NULL DEFAULT '',
  `phone` varchar(10) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table booking.drivers: ~1 rows (approximately)
INSERT INTO `drivers` (`id`, `name`, `license`, `phone`) VALUES
	(1, 'Duong Thanh Truong', '3121410546', '0386094783');

-- Dumping structure for table booking.ewalletpay
CREATE TABLE IF NOT EXISTS `ewalletpay` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `paymentid` int(11) NOT NULL,
  `provider` varchar(50) NOT NULL DEFAULT '0',
  `transactionid` int(11) NOT NULL,
  `status` int(11) NOT NULL,
  `time` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `FK_ewalletpay_payments` (`paymentid`),
  KEY `FK_ewalletpay_ewalletprovider` (`provider`),
  CONSTRAINT `FK_ewalletpay_payments` FOREIGN KEY (`paymentid`) REFERENCES `payments` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table booking.ewalletpay: ~1 rows (approximately)
INSERT INTO `ewalletpay` (`id`, `paymentid`, `provider`, `transactionid`, `status`, `time`) VALUES
	(1, 1, 'Momo', 1111, 0, '2024-09-15 14:11:18');

-- Dumping structure for table booking.payments
CREATE TABLE IF NOT EXISTS `payments` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bookingid` int(11) NOT NULL,
  `amount` double NOT NULL DEFAULT 0,
  `method` varchar(50) NOT NULL DEFAULT '',
  `time` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `FK_payments_bookings` (`bookingid`),
  CONSTRAINT `FK_payments_bookings` FOREIGN KEY (`bookingid`) REFERENCES `bookings` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table booking.payments: ~1 rows (approximately)
INSERT INTO `payments` (`id`, `bookingid`, `amount`, `method`, `time`) VALUES
	(1, 1, 320000, 'Ví điện tử', '2024-09-15 14:08:32');

-- Dumping structure for table booking.routes
CREATE TABLE IF NOT EXISTS `routes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `from` varchar(50) NOT NULL DEFAULT '',
  `to` varchar(50) NOT NULL DEFAULT '',
  `distance` int(11) NOT NULL DEFAULT 0,
  `duration` time DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table booking.routes: ~1 rows (approximately)
INSERT INTO `routes` (`id`, `from`, `to`, `distance`, `duration`) VALUES
	(1, 'TP Hồ Chí Minh', 'Phú Yên', 540, '10:00:00');

-- Dumping structure for table booking.schedules
CREATE TABLE IF NOT EXISTS `schedules` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `busid` int(11) NOT NULL,
  `routeid` int(11) NOT NULL,
  `departure` timestamp NOT NULL DEFAULT current_timestamp(),
  `arrival` timestamp NOT NULL DEFAULT current_timestamp(),
  `price` double NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `FK_schedules_buses` (`busid`),
  KEY `FK_schedules_routes` (`routeid`),
  CONSTRAINT `FK_schedules_buses` FOREIGN KEY (`busid`) REFERENCES `buses` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_schedules_routes` FOREIGN KEY (`routeid`) REFERENCES `routes` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table booking.schedules: ~1 rows (approximately)
INSERT INTO `schedules` (`id`, `busid`, `routeid`, `departure`, `arrival`, `price`) VALUES
	(1, 2, 1, '2024-09-15 12:00:00', '2024-09-14 22:00:00', 320000);

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
