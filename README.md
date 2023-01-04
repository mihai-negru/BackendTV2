# Negru Mihai 323CD: **POO TV**

A mini backend server processing simple requests from a client connected to the application.</br>
The server can connect one client at a time and processes actions just from one user. For further implementations</br>
this behaviour may be changed in order to process asynchronous different requests for more than one client.

## **Table of contents**

1. [Getting started](#start)
    1. [Project Structure](#proj-str)
    2. [Files Hierarchy](#files-hier)
2. [Storage Package](#storage)
3. [Server Package](#server)
4. [Client Package](#client)
5. [Parser Package](#parser)
6. [Pagestype Package](#pagetype)
7. [Process Package](#process)
    1. [Factory Design](#factory)
    2. [Command Design](#command)
8. [New Features](#new-features)
9. [Bonus](#bonus)
10. [Feedback](#feed)


<a name="start"></a>
## **Getting Started**

This *README* covers both first part of the project and the second part of the project<br>
In order to read the second part description navigate to the "[New Features](#new-features)"


<a name="proj-str"></a>
### **Project Structure**

The project is divided in two parts:
* *skeleton* - files provided by the teaching assistants in order to test our code.
* *non-skeleton* - files provided by me to solve this task.

In the *src* folder of the current project you can find more than one package regarding my implementation.

The Main file contains a basic function in order to fetch the input data from the Json file and to print the
output</br>
generated to another Json file. The reading of the input data is done the same it was done at
first homework</br>
assignment by the **oop team**.

For this reason you can find in the *datafetch* package all the input classes needed for parsing the
data from Json File</br> into special classes that will be processed later.

<a name="files-hier"></a>
### **Files Hierarchy**

All the effective code written in order to solve the problems can be found in the **backendtv** package</br>
which is represented in the following hierarchy:
* *storage* - contains basic workflow of a database.
* *server* - contains the main functionality of the server, which processes requests from a client.
* *client* - contains a basic representation of a client on the server.
* *parser* - utility class in order to parse the normal output into the Json File.
* *pagestype* - an enum class for pages representation.
* *process* - main class for processing all the actions from the input data, can be referred as the server's core.

In following sections we will discuss more about packages specified above.

<a name="storage"></a>
## **Storage Package**

Package contains the main functionality for storing the input data and modifying the data when a process occurs.
</br>
The package is divided in two classes:
* *DataCollection* - which represents a collection of data, user can insert, modify, delete some data, or some</br>members.
* *Database* - it is a wrapper for the collection class, in other words the Database class maintains the workflow
  of</br>the collections.

Package works like a real database (ex: mongodb), the collection class accepts as an input a Map object
filled with</br> pairs of type (**key**:String, **value**:String), which will represent the effective information
of a client or a movie. However,</br> the collection class does not care about what we will insert, it takes care
just for modifying the collection itself not the</br> data from it.

This behaviour is very useful, because if we will want to introduce a collection, we do not have to rewrite all
the</br> collection, we just need to create a different collection and to populate with a Map Object containing the
information needed.

As specified above the collection works on a String system, just like the real databases, meaning it can process
just</br> Strings not anything else, adding a field as an integer or another type, will cause an error, however for
storing an</br> integer or other type the easiest way is to parse the type into a String type.

The **Database** class is a **Singleton** class which encapsulates all the collections, and keeps track of them.</br>
It is impossible to access a collection without firstly accessing the database.

This class is designed as a **Singleton**, because in a real time application we should not have more than one instance</br>
of the database. The database is unique for all the clients, movies or anything else in order to process correctly</br>
the information stored.


<a name="server"></a>
## **Server Package**

This package is the entry point of the whole backend application, in order for a client to have access to the
server</br> resources it has to connect to the server via the ServerApp class.

The class is a **Singleton** for a very good reason, clients cannot create a new instance of the server, or by
anybody else,</br> the server exists and is running, any client that want to access the resources has to **CONNECT**
to server.

This behaviour is very suitable if we would have more than one client that wants to connect. If we want also</br>
communication, between client and communication between resources of clients the best way is to create the server</br>
class as a Singleton.

The main role of the class is to connect the client to the database and to manage the actions requested by the
input</br> data, also the client activity is tracked by the server.

<a name="client"></a>
## **Client Package**

A very simple package containing the basic workflow for a client.

A client can be of two types:
* *guest client* - has no rights to access the resources of application, can log or register.
* *active client* - a normal user of the platform.

Every user first is a guest, when it is logging or registering it becomes an active client.

The server does not track if the client is active or not, it is caring just about for the connection and actions
execution.

When the guest becomes a normal client, all the data about him is loaded in the class and all the non-restricted</br>
movies are loaded into the class.

An active client knows how to purchase, watch, like or rate a film so all these actions are encapsulated in the</br>
current class.


<a name="parser"></a>
## **Parser Package**

This package encapsulated the functionality in order to print the server output into a
Json File.

For now the class knows how to parse a user and a movie, however for further implementations it would be easy</br>
to add new functionalities.

The **JsonParser** class is a utility class, because the printing in the output Json File does not depend on
the instance</br> created by the class, the parsing remain same from the beginning until ending of the server execution.

<a name="pagetype"></a>
## **Pagestype Package**

A simple enum class containing the types of all pages that can be loaded by the server at a moment of time.

I decided not to create a utility class with a bunch of constants in it. The best way for me to work around</br>
was by using the enum class, which encapsulated very good the classes types.


<a name="process"></a>
## **Process Package**

Package contains the main workflow of the server, it is the core of it, the processor.

The processor is build using two design patterns:
* *Factory* - for encapsulating the creation of commands.
* *Command* - for executing the commands in a very cute way.

The main handler of the processes is represented by the **ContextAction** class.

At the creation of the Context class the actions input is processed and depending on some flags the factory</br>
class created all the handlers and puts them in the *ready queue* of the *ContextAction* class.

The class has two main functions that processes the ready queue:
* *hasNext* - checks if there are any action left to be handled
* *next* - calls the next handler for an action from the ready queue.

The handlers are called in the order of their creation from the factory.

<a name="factory"></a>
### **Factory Design**

The factory design pattern can be found under the *actionfactory* package.

The factory gets data about a given action from the input files and creates a handle for the given action,
which is then</br>returned to the **ContextAction** class instance and is put in the ready queue, in order to be called.

The factory looks at some flags like **type** flag and decides what handler to create.

<a name="command"></a>
### **Command Design**

The command design pattern can be found in the *actiontype** package.

All the specific commands implement the functional interface **ActionCommand**, which has
just one function **execute**,</br>the execute method calls the handler for a specific command.

More details about the code implementation may be found in the JavaDoc written for every specific command.

The creation of the command (factory step) initializes all the needed information for an action to occur.
When the handler</br>is called all the information is loaded into the handler, and it starts executing the code.

<a name="new-features"></a>
## **New Features**

<a name="bonus"></a>
## **Bonus**

* Usage of streams (just like the last homework).
* Usage of 3 designs patterns (Singleton, Command and Factory).
* Using a real-like database (instead of using a database with two lists of users and movies).
* Very generic code, written well for extension.
* Very easy code to extend in order to connect more than one client to the server.


<a name="feed"></a>
## **Feedback**

