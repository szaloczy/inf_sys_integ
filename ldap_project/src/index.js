import ldap from 'ldapjs'

const client = ldap.createClient({
  url: "ldap://localhost:389"
});

const userDN = "uid=jdoe,ou=users,dc=demo,dc=local";

client.bind(userDN, "admin", err => {
  if (err) {
    console.error("Authentication failed:", err.message);
  } else {
    console.log("✅ Authentication successful");
  }
  client.unbind();
});
