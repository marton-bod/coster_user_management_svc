INSERT INTO users (email_addr, first_name, last_name, password, registered, last_active)
  VALUES ('testaccount@test.com', 'Richard', 'Blake', 'secretive123', '2018-05-19', '2019-01-08');

INSERT INTO auth_tokens (user_id, auth_token, issued, expiry)
  VALUES ('testaccount@test.com', 'abcd-efgh1000', '2019-01-03', '2033-01-05');

INSERT INTO auth_tokens (user_id, auth_token, issued, expiry)
  VALUES ('testaccount2@test.com', 'xxxyyyzzz50', '2019-01-03', '2019-01-05');