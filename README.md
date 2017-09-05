DatabaseUtils
---

[![Build Status](https://travis-ci.org/DanielMajoinen/DatabaseUtils.svg)](https://travis-ci.org/DanielMajoinen/DatabaseUtils)

DatabaseUtils is a library providing easy initialisation, verification and access to JDBC databases. 
Currently there is only support for SQLite, but other JDBC connections are planned to be supported in the near future.

Configuration:
---
Each database connection will require its own `.properties` file which is named  after the database it is for. These property 
files are required to be placed in the `resources/config/` directory. Each database type requires 
its own specific properties, but all <strong>MUST</strong> define the database 
type.

Connect to database `dbutils` example: 

    resources/config/dbutils.properties

Sample SQLite properties file:

    database.type=SQLite
    database.directory=data
    database.file.extension=db

File based databases will require a template database located in 
`resources/databases/` which will be copied on init.

Example:

    resources/databases/dbutils.db

Usage:
---
#### Instantiate a Database Controller:

Instantiate a database controller which has the same name as its config file:

    DatabaseController databaseController = DatabaseControllerFactory.getController(DATABASE_NAME);

Instantiate a database controller which has a different name to its config file:

    DatabaseController databaseController = DatabaseControllerFactory.getController(DATABASE_NAME, CONFIG_FILENAME);

#### Initialisation:

To automatically create the database, the database controller must be initialised:

    databaseController.init();


#### INSERT:

  1. Prepare query
  2. Define optional parameters
  3. Execute update

<strong>NOTE:</strong> Parameters are defined as a `String` key and `Object` value where the key is a substring of the query.

     private static final String EXAMPLE_INSERT_QUERY = 
       "INSERT INTO `user` ('email', 'password') VALUES (:email, :password)";
    
    ...
    
     int affectedRows = databaseController
       .prepareQuery(EXAMPLE_INSERT_QUERY)
       .setParameter(":email", email)
       .setParameter(":password", password)
       .executeUpdate();

#### Batch INSERT:

  1. Prepare a batch query
  2. Define any optional parameters for the query
  3. Repeat the process for desired amount of queries
  3. Execute update
  
<strong>NOTE:</strong> Parameters must be defined after each query they are intended for and before preparing the next query.


     private static final String EXAMPLE_BATCH_INSERT_QUERY_1 = 
       "INSERT INTO `user` ('email', 'password') VALUES (:email, :password)";
       
     private static final String EXAMPLE_BATCH_INSERT_QUERY_2 = 
       "UPDATE `user` SET 'password' = :password WHERE 'email' = :email";
    
    ...
    
     int affectedRows = databaseController
       .prepareBatchQuery(EXAMPLE_BATCH_INSERT_QUERY_1)
       .setParameter(":email", email)
       .setParameter(":password", password)
       .prepareBatchQuery(EXAMPLE_BATCH_INSERT_QUERY_2)
       .setParameter(":password", password)
       .setParameter(":email", email)
       .executeUpdate();

#### SELECT & Map to Single Object:

 1. Prepare Query
 2. Define optional parameters for the query
 3. Execute
 4. Map using an `ObjectMapper` concrete class or using a lambda expression
 
Example using lambda expression:

    String sql = databaseController
      .prepareQuery(VERIFY_TABLE_QUERY)
      .setParameter(":tablename", tableName)
      .executeAndMap(resultSet ->
        resultSet.getString(VERIFY_TABLE_COLUMN_LABEL));

## Select & Map to a List:

 1. Prepare Query
 2. Define optional parameters for the query
 3. Execute
 4. Map all using an `ObjectMapper` concrete class or using a lambda expression
 
Example using lambda expression:

    List<String> list = databaseController
      .prepareQuery(SELECT_QUERY)
      .setParameter(":parameter", parameter)
      .executeAndMapAll(resultSet ->
        resultSet.getString(COLUMN_LABEL));
        
