# Reflection Assert

Library to make assertions on classes, methods and fields that need to exist or have a certain implementation.
Used for educational purposes, where an exercise expects a certain method to have been written

## Usage

To check that a class exists, use the following code:

```java
checkClass("com.switchfully.accounting.BankAccount")
```
---
TO check that a class has certain fields, use the following code:
```java
onClass("com.switchfully.accounting.BankAccount")
    .expectFields(
            field("accountNumber", String.class),
            field("name", String.class),
            field("balance", int.class)
    );
```

---
To check that a constructor exists and initializes fields, use the following code:
```java
onClass("com.switchfully.accounting.BankAccount")
    .callConstructor("anAccountNumber","aName")
    .expectField("accountNumber").toHaveValue("anAccountNumber")
    .expectField("name").toHaveValue("aName");
```

---
To check that a method exists, use the following code:
```java
onClass("com.switchfully.accounting.BankAccount")
    .callConstructor("anAccountNumber", "aName", 2000)
    .callMethod("getAccountNumber").expectReturnToBe("anAccountNumber").and()
    .callMethod("getName").expectReturnToBe("aName").and()
    .callMethod("getBalance").expectReturnToBe(2000);
```
