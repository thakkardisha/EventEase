-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 21, 2025 at 01:47 PM
-- Server version: 10.4.21-MariaDB
-- PHP Version: 8.0.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `eventease`
--

-- --------------------------------------------------------

--
-- Table structure for table `artists`
--

CREATE TABLE `artists` (
  `a_id` int(11) NOT NULL,
  `a_name` varchar(100) NOT NULL,
  `a_bio` text DEFAULT NULL,
  `a_imgUrl` varchar(255) DEFAULT NULL,
  `a_type` enum('Individual','Band') DEFAULT 'Individual'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `artists`
--

INSERT INTO `artists` (`a_id`, `a_name`, `a_bio`, `a_imgUrl`, `a_type`) VALUES
(2, 'Sonu Nigam', 'Evergreen', NULL, 'Individual'),
(3, 'Aditya Gadhvi', 'The voice of Gujarat', 'aditya.jepg', 'Individual');

-- --------------------------------------------------------

--
-- Table structure for table `artist_social_links`
--

CREATE TABLE `artist_social_links` (
  `l_id` int(11) NOT NULL,
  `a_id` int(11) NOT NULL,
  `platform` enum('Instagram','Youtube','','') DEFAULT 'Instagram',
  `link` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `audit_logs`
--

CREATE TABLE `audit_logs` (
  `log_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `action` enum('Insert','Update','Delete','Login','Logout','Status_Changed','Sign_in') NOT NULL,
  `table_name` varchar(50) NOT NULL,
  `record_id` int(11) NOT NULL,
  `old_values` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL CHECK (json_valid(`old_values`)),
  `new_values` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL CHECK (json_valid(`new_values`)),
  `date_time` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `bookings`
--

CREATE TABLE `bookings` (
  `b_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `e_id` int(11) NOT NULL,
  `ticket_count` int(11) NOT NULL,
  `total_amount` decimal(10,0) NOT NULL,
  `booking_date` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `bookings`
--

INSERT INTO `bookings` (`b_id`, `user_id`, `e_id`, `ticket_count`, `total_amount`, `booking_date`) VALUES
(3, 1, 1, 2, '750', '2025-11-09 21:30:50');

-- --------------------------------------------------------

--
-- Table structure for table `booking_coupons`
--

CREATE TABLE `booking_coupons` (
  `b_id` int(11) NOT NULL,
  `coupon_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `booking_coupons`
--

INSERT INTO `booking_coupons` (`b_id`, `coupon_id`) VALUES
(3, 2),
(3, 3);

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

CREATE TABLE `categories` (
  `c_id` int(11) NOT NULL,
  `c_name` varchar(100) NOT NULL,
  `c_description` text DEFAULT NULL,
  `c_img` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`c_id`, `c_name`, `c_description`, `c_img`) VALUES
(1, 'Music', NULL, NULL),
(2, 'Workshop', 'Learn together', 'workshop.jpeg');

-- --------------------------------------------------------

--
-- Table structure for table `coupons`
--

CREATE TABLE `coupons` (
  `c_id` int(11) NOT NULL,
  `c_code` varchar(20) NOT NULL,
  `discount_type` enum('percent','fixed') NOT NULL DEFAULT 'percent',
  `discount_value` decimal(10,0) NOT NULL,
  `max_uses` int(11) DEFAULT NULL,
  `used_count` int(11) DEFAULT NULL,
  `valid_from` date DEFAULT NULL,
  `valid_to` date DEFAULT NULL,
  `status` enum('active','expired','inactive') DEFAULT 'active',
  `is_single_use` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `coupons`
--

INSERT INTO `coupons` (`c_id`, `c_code`, `discount_type`, `discount_value`, `max_uses`, `used_count`, `valid_from`, `valid_to`, `status`, `is_single_use`) VALUES
(1, 'DISHA50', 'percent', '50', NULL, NULL, NULL, '2025-11-16', 'expired', 0),
(2, 'SONU15OFF', 'percent', '15', NULL, NULL, '2025-11-01', '2025-11-28', 'active', 0),
(3, 'WELCOME10', 'percent', '10', NULL, NULL, NULL, NULL, 'active', 1),
(4, 'AG200', 'fixed', '200', NULL, NULL, '2025-11-10', '2025-11-20', 'active', 0),
(5, 'MYCOUPON10', 'fixed', '10', 1, 1, '2025-11-11', '2025-11-12', 'inactive', 0);

-- --------------------------------------------------------

--
-- Table structure for table `coupon_usage`
--

CREATE TABLE `coupon_usage` (
  `usage_id` int(11) NOT NULL,
  `coupon_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `used_on` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `coupon_usage`
--

INSERT INTO `coupon_usage` (`usage_id`, `coupon_id`, `user_id`, `used_on`) VALUES
(1, 3, 1, '2025-11-09 16:00:50');

-- --------------------------------------------------------

--
-- Table structure for table `events`
--

CREATE TABLE `events` (
  `e_id` int(11) NOT NULL,
  `e_name` varchar(150) NOT NULL,
  `description` text NOT NULL,
  `event_date` date NOT NULL,
  `start_time` time NOT NULL,
  `end_time` time DEFAULT NULL,
  `unitPrice` decimal(10,2) NOT NULL,
  `v_id` int(11) NOT NULL,
  `c_id` int(11) NOT NULL,
  `max_capacity` int(11) DEFAULT NULL,
  `booked_seats` int(11) DEFAULT 0,
  `banner_img` varchar(255) NOT NULL,
  `status` enum('Active','Cancelled','Completed','Sold out') NOT NULL DEFAULT 'Active'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `events`
--

INSERT INTO `events` (`e_id`, `e_name`, `description`, `event_date`, `start_time`, `end_time`, `unitPrice`, `v_id`, `c_id`, `max_capacity`, `booked_seats`, `banner_img`, `status`) VALUES
(1, 'Sonu Nigam Musical', 'A night to remember', '2025-12-27', '09:00:00', NULL, '450.00', 1, 1, 0, NULL, 'sonuConcert.jpeg', 'Active'),
(3, 'Osman Mir Live', 'Gujarati evening', '2025-11-20', '00:15:00', '01:15:00', '500.00', 1, 1, 1000, NULL, 'osmanConcert.jpeg', 'Active'),
(4, 'Aditya Gadhvi Live In Concert', 'Global Gujarati', '2025-12-01', '20:00:00', '00:00:00', '800.00', 1, 1, 0, NULL, 'adityaConcert.jpeg', 'Active');

-- --------------------------------------------------------

--
-- Table structure for table `event_artists`
--

CREATE TABLE `event_artists` (
  `e_id` int(11) NOT NULL,
  `a_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `event_coupons`
--

CREATE TABLE `event_coupons` (
  `e_id` int(11) NOT NULL,
  `c_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `event_coupons`
--

INSERT INTO `event_coupons` (`e_id`, `c_id`) VALUES
(1, 2);

-- --------------------------------------------------------

--
-- Table structure for table `event_images`
--

CREATE TABLE `event_images` (
  `img_id` int(11) NOT NULL,
  `e_id` int(11) NOT NULL,
  `image_url` varchar(255) NOT NULL,
  `alt_text` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `event_images`
--

INSERT INTO `event_images` (`img_id`, `e_id`, `image_url`, `alt_text`) VALUES
(1, 1, 'aditya1.jpeg', 'aditya1');

-- --------------------------------------------------------

--
-- Table structure for table `interests`
--

CREATE TABLE `interests` (
  `i_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `e_id` int(11) NOT NULL,
  `interest_date` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `payments`
--

CREATE TABLE `payments` (
  `p_id` int(11) NOT NULL,
  `b_id` int(11) NOT NULL,
  `amount` decimal(10,0) NOT NULL,
  `transaction_id` varchar(50) NOT NULL,
  `payment_date` datetime DEFAULT current_timestamp(),
  `payment_status` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `payments`
--

INSERT INTO `payments` (`p_id`, `b_id`, `amount`, `transaction_id`, `payment_date`, `payment_status`) VALUES
(2, 3, '750', 'e2c11c68-e25a-4dbb-8e6d-cdc757f1d4f4', '2025-11-09 21:30:50', 'SUCCESS');

-- --------------------------------------------------------

--
-- Table structure for table `reviews`
--

CREATE TABLE `reviews` (
  `r_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `e_id` int(11) NOT NULL,
  `review` text NOT NULL,
  `r_date` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `tickets`
--

CREATE TABLE `tickets` (
  `t_id` int(11) NOT NULL,
  `b_id` int(11) NOT NULL,
  `ticket_number` varchar(20) NOT NULL,
  `qr_code` varchar(255) NOT NULL,
  `ticket_type` varchar(20) NOT NULL DEFAULT 'Regular',
  `price` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `tickets`
--

INSERT INTO `tickets` (`t_id`, `b_id`, `ticket_number`, `qr_code`, `ticket_type`, `price`) VALUES
(1, 3, 'TKT-1762704050590-1', 'c52389c1-2df1-405a-a235-a1b2a9004d6f', 'General', '500.00'),
(2, 3, 'TKT-1762704050595-2', '362131bf-dcb3-42e2-abdb-62ac51bf1e7b', 'General', '500.00');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `fullName` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone` int(20) NOT NULL,
  `group_id` int(11) NOT NULL DEFAULT 2
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `username`, `fullName`, `email`, `password`, `phone`, `group_id`) VALUES
(1, 'Dhruv', 'Dhruv Thakkar', 'dhruv@gmail.com', 'dhruvSecret', 987654321, 2),
(5, 'purna', 'Purna', 'p@gmail.com', 'PBKDF2WithHmacSHA256:2048:NsP//EVO7dK1WKirHrMHVFZiaxgZFtRT3+sCCiQsaDM=:R/6O7vzDWFBcj2o9PpTK8C07ufYbNZXT0E0auQlZVM8=', 978645312, 2),
(6, 'admin', 'admin', 'admin@gmail.com', 'PBKDF2WithHmacSHA256:2048:cU6NA7H6lZItMgOv2ovvzLlGPp3xMMLgJ6IfPy6AmTc=:9kP0ZR4T41iM7ot95/9rTKL89NE1mrplC1LpC735eiQ=', 963852744, 1),
(10, 'vipul', 'Vipul T', 'vipul@gmail.com', 'PBKDF2WithHmacSHA256:2048:w44dc81CW7+hbgqcmJQ8z6wonZ/3cPWhVmXzXevmajQ=:w+HJt5ntQk9ccspigM0Pb+OQRKOYe7u1JSGEqBiyFEg=', 987412365, 2),
(25, 'dhruvvvv', 'Dhruv Thakkar', 'thakkardhhruv@gmail.com', 'PBKDF2WithHmacSHA256:2048:1VPZUgkhNKTrhhs0xxzSlNL4+WrY4dLabHGgeNaKOSg=:T47r4T0fVLBNv1gykt+dQoFdD3XaaxkWNFR687zsQnM=', 728503384, 2),
(26, 'vips', 'vipul', 'vipuljthakkar@gmail.com', 'PBKDF2WithHmacSHA256:2048:Vda+b2A4ruKy596PbCnAi302lyy9/af7TGtrHtbeces=:SwMSnUEdhueaz0c0svqEQIQu5bg0ahOBOdSukekDI4w=', 639852147, 2);

-- --------------------------------------------------------

--
-- Table structure for table `user_group_master`
--

CREATE TABLE `user_group_master` (
  `group_id` int(11) NOT NULL,
  `group_name` enum('Admin','User','Guest') DEFAULT 'User',
  `username` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `user_group_master`
--

INSERT INTO `user_group_master` (`group_id`, `group_name`, `username`) VALUES
(1, 'Admin', 'AdminDisha'),
(2, 'User', 'User');

-- --------------------------------------------------------

--
-- Table structure for table `venues`
--

CREATE TABLE `venues` (
  `v_id` int(11) NOT NULL,
  `v_name` varchar(100) NOT NULL,
  `v_address` text DEFAULT NULL,
  `v_city` varchar(20) NOT NULL,
  `v_state` varchar(25) NOT NULL,
  `v_capacity` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `venues`
--

INSERT INTO `venues` (`v_id`, `v_name`, `v_address`, `v_city`, `v_state`, `v_capacity`) VALUES
(1, 'Indoor Stadium', 'God-dod road', 'Surat', 'Gujarat', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `wishlists`
--

CREATE TABLE `wishlists` (
  `w_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `e_id` int(11) NOT NULL,
  `added_date` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `artists`
--
ALTER TABLE `artists`
  ADD PRIMARY KEY (`a_id`);

--
-- Indexes for table `artist_social_links`
--
ALTER TABLE `artist_social_links`
  ADD PRIMARY KEY (`l_id`),
  ADD KEY `artist_social_links_fk_a_id` (`a_id`);

--
-- Indexes for table `audit_logs`
--
ALTER TABLE `audit_logs`
  ADD PRIMARY KEY (`log_id`),
  ADD KEY `audit_logs_fk_user_id` (`user_id`);

--
-- Indexes for table `bookings`
--
ALTER TABLE `bookings`
  ADD PRIMARY KEY (`b_id`),
  ADD KEY `bookings_fk_user_id` (`user_id`),
  ADD KEY `bookings_fk_e_id` (`e_id`);

--
-- Indexes for table `booking_coupons`
--
ALTER TABLE `booking_coupons`
  ADD PRIMARY KEY (`b_id`,`coupon_id`),
  ADD KEY `booking_coupons_fk_coupon_id` (`coupon_id`);

--
-- Indexes for table `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`c_id`);

--
-- Indexes for table `coupons`
--
ALTER TABLE `coupons`
  ADD PRIMARY KEY (`c_id`),
  ADD UNIQUE KEY `c_code` (`c_code`);

--
-- Indexes for table `coupon_usage`
--
ALTER TABLE `coupon_usage`
  ADD PRIMARY KEY (`usage_id`),
  ADD KEY `coupon_usage_fk_coupon_id` (`coupon_id`),
  ADD KEY `coupon_usage_fk_user_id` (`user_id`);

--
-- Indexes for table `events`
--
ALTER TABLE `events`
  ADD PRIMARY KEY (`e_id`),
  ADD KEY `events_fk_v_id` (`v_id`),
  ADD KEY `events_fk_c_id` (`c_id`);

--
-- Indexes for table `event_artists`
--
ALTER TABLE `event_artists`
  ADD PRIMARY KEY (`e_id`,`a_id`),
  ADD KEY `event_artists_fk_a_id` (`a_id`);

--
-- Indexes for table `event_coupons`
--
ALTER TABLE `event_coupons`
  ADD PRIMARY KEY (`e_id`,`c_id`),
  ADD KEY `event_coupons_fk_c_id` (`c_id`);

--
-- Indexes for table `event_images`
--
ALTER TABLE `event_images`
  ADD PRIMARY KEY (`img_id`),
  ADD KEY `event_images_fk_e_id` (`e_id`);

--
-- Indexes for table `interests`
--
ALTER TABLE `interests`
  ADD PRIMARY KEY (`i_id`),
  ADD KEY `interests_fk_user_id` (`user_id`),
  ADD KEY `interests_fk_e_id` (`e_id`);

--
-- Indexes for table `payments`
--
ALTER TABLE `payments`
  ADD PRIMARY KEY (`p_id`),
  ADD UNIQUE KEY `transaction_id` (`transaction_id`),
  ADD KEY `payments_fk_b_id` (`b_id`);

--
-- Indexes for table `reviews`
--
ALTER TABLE `reviews`
  ADD PRIMARY KEY (`r_id`),
  ADD KEY `reviews_fk_user_id` (`user_id`),
  ADD KEY `reviews_fk_e_id` (`e_id`);

--
-- Indexes for table `tickets`
--
ALTER TABLE `tickets`
  ADD PRIMARY KEY (`t_id`),
  ADD UNIQUE KEY `ticket_number` (`ticket_number`),
  ADD UNIQUE KEY `qr_code` (`qr_code`),
  ADD KEY `tickets_fk_b_id` (`b_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `phone` (`phone`),
  ADD KEY `users_fk_group_id` (`group_id`);

--
-- Indexes for table `user_group_master`
--
ALTER TABLE `user_group_master`
  ADD PRIMARY KEY (`group_id`);

--
-- Indexes for table `venues`
--
ALTER TABLE `venues`
  ADD PRIMARY KEY (`v_id`);

--
-- Indexes for table `wishlists`
--
ALTER TABLE `wishlists`
  ADD PRIMARY KEY (`w_id`),
  ADD KEY `wishlists_fk_user_id` (`user_id`),
  ADD KEY `wishlists_fk_e_id` (`e_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `artists`
--
ALTER TABLE `artists`
  MODIFY `a_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `artist_social_links`
--
ALTER TABLE `artist_social_links`
  MODIFY `l_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `audit_logs`
--
ALTER TABLE `audit_logs`
  MODIFY `log_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `bookings`
--
ALTER TABLE `bookings`
  MODIFY `b_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `categories`
--
ALTER TABLE `categories`
  MODIFY `c_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `coupons`
--
ALTER TABLE `coupons`
  MODIFY `c_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `coupon_usage`
--
ALTER TABLE `coupon_usage`
  MODIFY `usage_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `events`
--
ALTER TABLE `events`
  MODIFY `e_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `event_images`
--
ALTER TABLE `event_images`
  MODIFY `img_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `interests`
--
ALTER TABLE `interests`
  MODIFY `i_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `payments`
--
ALTER TABLE `payments`
  MODIFY `p_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `reviews`
--
ALTER TABLE `reviews`
  MODIFY `r_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tickets`
--
ALTER TABLE `tickets`
  MODIFY `t_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=27;

--
-- AUTO_INCREMENT for table `user_group_master`
--
ALTER TABLE `user_group_master`
  MODIFY `group_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `venues`
--
ALTER TABLE `venues`
  MODIFY `v_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `wishlists`
--
ALTER TABLE `wishlists`
  MODIFY `w_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `artist_social_links`
--
ALTER TABLE `artist_social_links`
  ADD CONSTRAINT `artist_social_links_fk_a_id` FOREIGN KEY (`a_id`) REFERENCES `artists` (`a_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `audit_logs`
--
ALTER TABLE `audit_logs`
  ADD CONSTRAINT `audit_logs_fk_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `bookings`
--
ALTER TABLE `bookings`
  ADD CONSTRAINT `bookings_fk_e_id` FOREIGN KEY (`e_id`) REFERENCES `events` (`e_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `bookings_fk_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `booking_coupons`
--
ALTER TABLE `booking_coupons`
  ADD CONSTRAINT `booking_coupons_fk_b_id` FOREIGN KEY (`b_id`) REFERENCES `bookings` (`b_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `booking_coupons_fk_coupon_id` FOREIGN KEY (`coupon_id`) REFERENCES `coupons` (`c_id`) ON DELETE CASCADE;

--
-- Constraints for table `coupon_usage`
--
ALTER TABLE `coupon_usage`
  ADD CONSTRAINT `coupon_usage_fk_coupon_id` FOREIGN KEY (`coupon_id`) REFERENCES `coupons` (`c_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `coupon_usage_fk_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `events`
--
ALTER TABLE `events`
  ADD CONSTRAINT `events_fk_c_id` FOREIGN KEY (`c_id`) REFERENCES `categories` (`c_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `events_fk_v_id` FOREIGN KEY (`v_id`) REFERENCES `venues` (`v_id`) ON DELETE CASCADE;

--
-- Constraints for table `event_artists`
--
ALTER TABLE `event_artists`
  ADD CONSTRAINT `event_artists_fk_a_id` FOREIGN KEY (`a_id`) REFERENCES `artists` (`a_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `event_artists_fk_e_id` FOREIGN KEY (`e_id`) REFERENCES `events` (`e_id`) ON DELETE CASCADE;

--
-- Constraints for table `event_coupons`
--
ALTER TABLE `event_coupons`
  ADD CONSTRAINT `event_coupons_fk_c_id` FOREIGN KEY (`c_id`) REFERENCES `coupons` (`c_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `event_coupons_fk_e_id` FOREIGN KEY (`e_id`) REFERENCES `events` (`e_id`) ON DELETE CASCADE;

--
-- Constraints for table `event_images`
--
ALTER TABLE `event_images`
  ADD CONSTRAINT `event_images_fk_e_id` FOREIGN KEY (`e_id`) REFERENCES `events` (`e_id`) ON DELETE CASCADE;

--
-- Constraints for table `interests`
--
ALTER TABLE `interests`
  ADD CONSTRAINT `interests_fk_e_id` FOREIGN KEY (`e_id`) REFERENCES `events` (`e_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `interests_fk_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `payments`
--
ALTER TABLE `payments`
  ADD CONSTRAINT `payments_fk_b_id` FOREIGN KEY (`b_id`) REFERENCES `bookings` (`b_id`) ON DELETE CASCADE;

--
-- Constraints for table `reviews`
--
ALTER TABLE `reviews`
  ADD CONSTRAINT `reviews_fk_e_id` FOREIGN KEY (`e_id`) REFERENCES `events` (`e_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `reviews_fk_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `tickets`
--
ALTER TABLE `tickets`
  ADD CONSTRAINT `tickets_fk_b_id` FOREIGN KEY (`b_id`) REFERENCES `bookings` (`b_id`) ON DELETE CASCADE;

--
-- Constraints for table `users`
--
ALTER TABLE `users`
  ADD CONSTRAINT `users_fk_group_id` FOREIGN KEY (`group_id`) REFERENCES `user_group_master` (`group_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `wishlists`
--
ALTER TABLE `wishlists`
  ADD CONSTRAINT `wishlists_fk_e_id` FOREIGN KEY (`e_id`) REFERENCES `events` (`e_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `wishlists_fk_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
