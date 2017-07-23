DatabaseUtils
---
DatabaseUtils is a library providing easy initialisation, verification and access to JDBC databases. 
Currently there is only support for SQLite, but other JDBC connections are planned to be supported in the near future.

Configuration:
---
Each database connection will require its own `.properties` file which is named the same as the database. These property files are required to be placed in the `resources/config/` directory. Each database type requires 
its own specific properties, but all <strong>MUST</strong> define the database 
type.

Connect to database `dbutils` example: 

    resources/config/dbutils.properties

Sample SQLite properties file:

    database.type=SQLite
    database.directory=data
    database.file.extension=db
    
    table.names.delimiter=:
    table.names=user

Database Initialisation:
---

To automatically create and verify tables the database must be initialised:

    databaseController.init();

###### Table Creation & Verification
For each table defined in the configuration, a corresponding sql file must be placed in `resources/sql/`, with the query to create the table as its contents. This will be used on instantiation of the database controller where each table will be verified against the appropriate sql file. If the table does not exist, the contents will be used to create the table. If the table does exist but fails verification an exception will be thrown.

Example: `resources/sql/user.sql`

    CREATE TABLE `user`
        (`id` int,
         `name` char[15] NOT NULL,
         `password` char[36] NOT NULL,
         PRIMARY KEY(`id`))

###### Insert On Creation
An insert sql file can optionally be included for each table. This should contain insert queries for the corresponding table and will <strong>ONLY</strong> be run after creation of the table. The insert file must be named after the table with `-insert` appended to its name.

Example:

    resources/sql/user-insert.sql

<strong>NOTE:</strong> Insert queries should be separated with a `;`

    INSERT INTO `user` ('name', 'password') VALUES ('user1', 'pass');
    INSERT INTO `user` ('name', 'password') VALUES ('user2', 'pass')

Usage:
---
#### Instantiate a Database Controller:

    DatabaseController databaseController = DatabaseControllerFactory.getController(DATABASE_NAME);

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

    List<String> sql = databaseController
      .prepareQuery(SELECT_QUERY)
      .setParameter(":parameter", parameter)
      .executeAndMapAll(resultSet ->
        resultSet.getString(COLUMN_LABEL));
        