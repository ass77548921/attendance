-- Make notification_recipients.name nullable: entity does not expose this column
ALTER TABLE notification_recipients MODIFY COLUMN name VARCHAR(200) NULL;
