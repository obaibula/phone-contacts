INSERT INTO users (username, password, role)
VALUES('oleh123', 'abcAbc1##', 'USER');

INSERT INTO contacts (name, user_id)
VALUES('Petro', 1), ('Vasyl', 1);

INSERT INTO emails (email, contact_id)
VALUES ('petro1@mail.com', 1), ('vasyl1@mail.com', 2), ('vasyl2@mail.com', 2);

INSERT INTO phone_numbers (phone_number, contact_id)
VALUES ('+38 050 123-45-67', 1), ('+38 067 123-45-67', 1), ('+38 033 223-45-67', 2);