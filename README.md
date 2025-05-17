# Mini Java IDE
A mini java project that aims to create a working IDE like VS code

## API's and libraries imported
  JavaFX <br>
  Richtext <br>
  Java IO <br>
  css <br>  
  
  

## Install Guide

**Windows Install Guide:**
1) Clone project into your code editor
2) Open terminal, navigate to your local repository and make sure maven exists using: <br> <br>
  
   ```
   mvn -version
   ```
   
   <br>
   
3) Build the maven project :<br><br>
    
    ```
    mvn clean install
    ```
    
    <br>

   
   <br>
   
4) Run the application: <br><br>
    
    ```
    mvn clean javafx:run
    ```

   <br>
   

<br>


## IDE Features 
This project simulates the working of a java IDE with the following operations:
  - Editor to edit code <br>
  - Syntax highlighting for key words in the editor <br>
  - Console to display output <br>
  - Inbuilt compiler and debugger  <br>
  - Ability to create new tabs in the text editor <br>
  - File explorer that can be used to track and handle multiple java files <br>
  - Open local files and folders <br>
  - Run code on the current open editor <br>
  - Switch between light mode and dark mode <br>
  - Open terminal <br>

## IDE Features
The order is as follows from top to bottom:
- merge PDF
- Convert PDF to Word
- Compress PDF
- Encrypt PDF
- Decrypt PDF

## Simple Use Case examples
You can try simple commands in your terminal that perform a single operation as follows: 

-  #### To Merge 2 PDF's and save it with a name you specify:<br>

```
  python main.py -m -f "path\to\your\pdf1" "path\to\your\pdf2" -s 'merged-pdf.pdf'
```
<br>

-  #### To Encrypt a PDF:<br>

 ```
  python main.py -f "path\to\your\pdf" -e 'password-to-encrypt-with'
 ``` 
   <br>
   
-  #### To convert a PDF to a Word Document:<br>

  ```
  python main.py -f "path\to\your\pdf" -w
  ``` 
   <br>
   
-  #### To decrypt a file:<br>

  ```
  python main.py -f "path\to\your\encrypted\pdf" -d 'password-to-decrypt-with'
  ```
   <br>
   
-  #### To compress a PDF file:<br>

  ```
  python main.py -f "path\to\your\pdf" -c
  ``` 
 <br>
 
## Complex Use Case Examples

-  #### Merging and encrypting PDF files:<br>
```
  python main.py -m -f "path\to\your\pdf" "path\to\your\pdf" -s "output-pdf.pdf" -e "password-to-encrypt-with"
```
<br>
