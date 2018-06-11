-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema BOOKSTORE
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema BOOKSTORE
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `BOOKSTORE` DEFAULT CHARACTER SET utf8 ;
USE `BOOKSTORE` ;

-- -----------------------------------------------------
-- Table `BOOKSTORE`.`PUBLISHER`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `BOOKSTORE`.`PUBLISHER` (
  `PUBLISHER_NAME` VARCHAR(100) NOT NULL,
  `ADDRESS` VARCHAR(100) NULL,
  `TELEPHONE_NUMBER` VARCHAR(100) NULL,
  PRIMARY KEY (`PUBLISHER_NAME`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `BOOKSTORE`.`BOOK`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `BOOKSTORE`.`BOOK` (
  `ISBN` CHAR(13) NOT NULL,
  `PUBLISHER_NAME` VARCHAR(100) NOT NULL,
  `BOOK_TITLE` VARCHAR(100) NOT NULL,
  `BOOKS_IN_STOCK` INT NOT NULL,
  `MIN_THRESHOLD` INT NOT NULL,
  `PUBLICATION_YEAR` YEAR NOT NULL,
  `PRICE` REAL NOT NULL,
  `CATEGORY` VARCHAR(45) NOT NULL,
  `IMAGE_PATH` VARCHAR(400), -- Relative to resources/book/images/
  `RATING` REAL DEFAULT 2.5,
  PRIMARY KEY (`ISBN`),
  CONSTRAINT `BOOK_PUBLISHER_FK`
    FOREIGN KEY (`PUBLISHER_NAME`)
    REFERENCES `BOOKSTORE`.`PUBLISHER` (`PUBLISHER_NAME`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `BOOK_CATEGORY_FK`
	FOREIGN KEY (`CATEGORY`)
    REFERENCES `BOOKSTORE`.`BOOK_CATEGORIES` (`CATEGORY`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `BOOKSTORE`.`BOOK_AUTHORS`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `BOOKSTORE`.`BOOK_AUTHORS` (
  `ISBN` CHAR(13) NOT NULL,
  `AUTHOR_NAME` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`ISBN`, `AUTHOR_NAME`),
  CONSTRAINT `AUTHORS_BOOK_FK`
    FOREIGN KEY (`ISBN`)
    REFERENCES `BOOKSTORE`.`BOOK` (`ISBN`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

DROP TABLE IF EXISTS BOOK_ORDERS;
-- -----------------------------------------------------
-- Table `BOOKSTORE`.`BOOK_ORDERS`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `BOOKSTORE`.`BOOK_ORDERS` (
  `ISBN` CHAR(13) NOT NULL,
  `QUANTITY` INT NOT NULL,
  `PUBLISHER_NAME` VARCHAR(100) NOT NULL,
  `ORDER_NO` INT NOT NULL AUTO_INCREMENT,
  `ORDER_DATE` TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
  INDEX `ORDERS_PUBLISHER_FK_idx` (`PUBLISHER_NAME` ASC),
  PRIMARY KEY (`ORDER_NO`),
  UNIQUE INDEX `ORDER_NO_UNIQUE` (`ORDER_NO` ASC),
  CONSTRAINT `ORDERS_BOOK_FK`
    FOREIGN KEY (`ISBN`)
    REFERENCES `BOOKSTORE`.`BOOK` (`ISBN`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `ORDERS_PUBLISHER_FK`
    FOREIGN KEY (`PUBLISHER_NAME`)
    REFERENCES `BOOKSTORE`.`PUBLISHER` (`PUBLISHER_NAME`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

DROP TABLE IF EXISTS BOOKSTORE.USER_PURCHASES;

DROP TABLE IF EXISTS BOOKSTORE.BOOKSTORE_USER;

CREATE TABLE IF NOT EXISTS `BOOKSTORE`.`BOOKSTORE_USER` (
  `USER_ID` INT NOT NULL AUTO_INCREMENT,
  `USER_NAME` VARCHAR(100) NOT NULL,
  `EMAIL` VARCHAR(100) UNIQUE NOT NULL,
  `PASSWORD` VARCHAR(100) NOT NULL,
  `USER_GROUP` VARCHAR(100) NOT NULL,
  `FIRST_NAME` VARCHAR(100),
  `LAST_NAME` VARCHAR(100),
  `BIRTH_DATE` DATE,
  `PHONE_NUMBER` VARCHAR(100),
  `SHIPPING_ADDRESS` VARCHAR(200),
  PRIMARY KEY (`USER_ID`))
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `BOOKSTORE`.`USER_PURCHASES` (
  `PURCHASE_ID` INT NOT NULL AUTO_INCREMENT,
  `USER_ID` INT NOT NULL,
  `ISBN` VARCHAR(100) NOT NULL,
  `PURCHASE_DATE` TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
  `QUANTITY` INT NOT NULL,
  `TOTAL_COST` FLOAT NOT NULL,
  CONSTRAINT `PURCHASES_BOOK_FK`
    FOREIGN KEY (`ISBN`)
    REFERENCES `BOOKSTORE`.`BOOK` (`ISBN`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `USER_PURCHASES_FK`
  FOREIGN KEY (`USER_ID`)
  REFERENCES `BOOKSTORE`.`BOOKSTORE_USER` (`USER_ID`)
  ON DELETE CASCADE
  ON UPDATE CASCADE,
  PRIMARY KEY (`PURCHASE_ID`))
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS BOOK_CATEGORIES (
  `CATEGORY` VARCHAR(45) NOT NULL,
  CONSTRAINT BOOK_CATEGORIES_PK
  PRIMARY KEY (`CATEGORY`))
ENGINE = InnoDB;

USE `BOOKSTORE`;

DELIMITER $$

DROP PROCEDURE IF EXISTS UPDATE_PASSWORD$$

CREATE PROCEDURE UPDATE_PASSWORD(USER_ID INT, OLD_PASSWORD VARCHAR(100),
								NEW_PASSWORD VARCHAR(100), OUT SUCCESS BOOLEAN,
                                OUT ERROR_MESSAGE VARCHAR(200))
BEGIN
	DECLARE CUR_PASSWORD VARCHAR(100);

    DECLARE EXIT HANDLER FOR SQLEXCEPTION 
	BEGIN
		ROLLBACK;
        GET DIAGNOSTICS CONDITION 1
			ERROR_MESSAGE = MESSAGE_TEXT;
        SET SUCCESS = FALSE;
	END;
		
	START TRANSACTION;
        SELECT BOOKSTORE_USER.PASSWORD INTO CUR_PASSWORD FROM BOOKSTORE_USER WHERE BOOKSTORE_USER.USER_ID = USER_ID FOR UPDATE;
		IF (OLD_PASSWORD = CUR_PASSWORD)
		THEN
			UPDATE BOOKSTORE_USER SET BOOKSTORE_USER.PASSWORD = NEW_PASSWORD WHERE BOOKSTORE_USER.USER_ID = USER_ID;
			COMMIT;
            SET SUCCESS = TRUE;
		ELSE
			ROLLBACK;
            SET ERROR_MESSAGE = "Error: old password is incorrect";
            SET SUCCESS = FALSE;
		END IF;
END$$

DROP PROCEDURE IF EXISTS MAKE_PURCHASE$$

CREATE PROCEDURE MAKE_PURCHASE(USER_ID INT, ISBN VARCHAR(100),
								QUANTITY INT, OUT SUCCESS BOOLEAN,
                                OUT PURCHASE_ID INT, OUT TOTAL_COST FLOAT, OUT ERROR_MESSAGE VARCHAR(200))
BEGIN
	DECLARE ITEM_PRICE FLOAT;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION 
	BEGIN
		GET DIAGNOSTICS CONDITION 1
			ERROR_MESSAGE = MESSAGE_TEXT;
        SET SUCCESS = FALSE;
	END;

    SELECT BOOK.PRICE INTO ITEM_PRICE FROM BOOK WHERE BOOK.ISBN = ISBN;
    IF (ITEM_PRICE IS NOT NULL)
    THEN
        SET TOTAL_COST = ITEM_PRICE * QUANTITY;
        UPDATE BOOK SET BOOK.BOOKS_IN_STOCK = BOOK.BOOKS_IN_STOCK - QUANTITY WHERE BOOK.ISBN = ISBN;
        INSERT INTO USER_PURCHASES (USER_ID, ISBN, QUANTITY, TOTAL_COST)
        VALUES (USER_ID, ISBN, QUANTITY, TOTAL_COST);
        SET PURCHASE_ID = LAST_INSERT_ID();
        SET SUCCESS = TRUE;
    ELSE
        SET ERROR_MESSAGE = "No such book exists in the library!";
        SET SUCCESS = FALSE;
    END IF;
END$$


DROP TRIGGER IF EXISTS CHECK_QUANTITY$$

CREATE TRIGGER CHECK_QUANTITY BEFORE UPDATE ON BOOK
FOR EACH ROW
BEGIN
    IF (NEW.BOOKS_IN_STOCK < 0)
    THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: Not enough books in stock.';
    END IF;
END$$

DROP TRIGGER IF EXISTS ORDER_BOOK$$

CREATE TRIGGER ORDER_BOOK AFTER UPDATE ON BOOK
FOR EACH ROW
BEGIN
    IF (NEW.BOOKS_IN_STOCK < NEW.MIN_THRESHOLD AND OLD.BOOKS_IN_STOCK >= NEW.MIN_THRESHOLD)
    THEN
        INSERT INTO
        BOOK_ORDERS(ISBN, PUBLISHER_NAME, QUANTITY)
        VALUES(NEW.ISBN, NEW.PUBLISHER_NAME, NEW.MIN_THRESHOLD - NEW.BOOKS_IN_STOCK);
    END IF;
END$$

DROP TRIGGER IF EXISTS ORDER_CONFIRMED$$

CREATE TRIGGER ORDER_CONFIRMED BEFORE DELETE ON BOOK_ORDERS
FOR EACH ROW
BEGIN
    UPDATE BOOK
    SET BOOK.BOOKS_IN_STOCK = BOOK.BOOKS_IN_STOCK + OLD.QUANTITY
    WHERE BOOK.ISBN = OLD.ISBN;
END$$


DELIMITER ;

--- INDEX ---
ALTER TABLE BOOK DROP INDEX IF EXISTS `ISBN_UNIQUE`;
ALTER TABLE BOOK ADD INDEX `TITLE_INDEX` (`BOOK_TITLE` ASC);
ALTER TABLE BOOK ADD INDEX `PRICE_INDEX` (`PRICE` ASC);
ALTER TABLE BOOK ADD INDEX `CATEGORY_INDEX` (`CATEGORY` ASC);
ALTER TABLE BOOK ADD INDEX `BOOK_PUBLISHER_FK_idx` (`PUBLISHER_NAME` ASC);
ALTER TABLE BOOK_AUTHORS ADD INDEX `AUTHOR_NAME_INDEX` (`AUTHOR_NAME` ASC);

INSERT INTO BOOK_CATEGORIES VALUES ('Art'), ('Science'), ('Religion'), ('History'), ('Geography');

ALTER TABLE BOOK ADD CONSTRAINT `BOOK_CATEGORY_FK`
	FOREIGN KEY (`CATEGORY`)
    REFERENCES `BOOKSTORE`.`BOOK_CATEGORIES` (`CATEGORY`)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
