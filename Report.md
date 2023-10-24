Assignment 2
------------

# Team Members

- Nicolas Montani
- Sebastian Oes

# GitHub link to your (forked) repository

https://github.com/sebioes/Assignment2

# Task 1

1. Indicate the time necessary for the SimpleCrawler to work.

Ans: ~ 78 seconds

# Task 2

1. Is the flipped index smaller or larger than the initial index? What does this depend on?

Ans: Larger. It depends on the number of **unique** words in the index.

# Task 3

1. Explain your design choices for the API design.

Ans:

1. Endpoint Structure:
   I used the "/admin" path to distinguish administrative functionality from regular search functionality ("/search"). This separation makes it clear that these endpoints are meant for administrative actions.

2. HTTP Methods:
   For launching a new crawling operation and regenerating the index, I used HTTP POST methods. These actions result in updates to the system, and POST is suitable for initiating such changes.
   For deleting a URL and updating URL information, I also used HTTP POST. This choice allows administrators to send data and instructions in the request body.

3. Use of Descriptive Summary:
   I provided concise and descriptive "summary" fields for each endpoint to make it clear what each endpoint does. This helps developers and administrators understand the purpose of each operation.

4. Parameters and Request Body:
   For the "/admin/delete-url" and "/admin/update-url-info" endpoints, I included "url" as a query parameter to specify which URL to delete or update. This choice simplifies the request URL and is more user-friendly.
   The "/admin/update-url-info" endpoint uses a request body with the "application/json" content type. This design allows administrators to send structured data for updating or adding information about a URL.

5. Response Codes and Descriptions:
   I used standard HTTP response codes (e.g., 200) with clear and informative descriptions. This aids in understanding the outcome of each operation.
   The response descriptions help in debugging and provide feedback to the administrator regarding the success or failure of the action.

# Task 4

1.  Indicate the time necessary for the MultithreadCrawler to work.

Ans: ~ 10 seconds

3. Indicate the ratio of the time for the SimpleCrawler divided by the time for the MultithreadedCrawler to get the increase in speed.

Ans: 78s/10s = 7.8





