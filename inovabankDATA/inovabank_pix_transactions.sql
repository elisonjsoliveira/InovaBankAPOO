CREATE DATABASE  IF NOT EXISTS `inovabank` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `inovabank`;
-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: localhost    Database: inovabank
-- ------------------------------------------------------
-- Server version	8.0.42

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `pix_transactions`
--

DROP TABLE IF EXISTS `pix_transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pix_transactions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `dateTime` datetime(6) NOT NULL,
  `keyTypeUsed` enum('CPF','EMAIL','PHONE','RANDOM') NOT NULL,
  `pixKeyUsed` varchar(255) NOT NULL,
  `status` enum('PENDING','COMPLETED','FAILED','REFUNDED') NOT NULL,
  `value` decimal(19,2) NOT NULL,
  `destination_account_id` bigint DEFAULT NULL,
  `origin_account_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKg1jf283v8ymgd6d7p9cb97t13` (`destination_account_id`),
  KEY `FK4vp5kgj2bvqlyqggklpt407ng` (`origin_account_id`),
  CONSTRAINT `FK4vp5kgj2bvqlyqggklpt407ng` FOREIGN KEY (`origin_account_id`) REFERENCES `account` (`id`),
  CONSTRAINT `FKg1jf283v8ymgd6d7p9cb97t13` FOREIGN KEY (`destination_account_id`) REFERENCES `account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pix_transactions`
--

LOCK TABLES `pix_transactions` WRITE;
/*!40000 ALTER TABLE `pix_transactions` DISABLE KEYS */;
/*!40000 ALTER TABLE `pix_transactions` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-29 14:40:44
