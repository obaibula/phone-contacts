## About
This project was created in accordance with the provided testing task.
You need to have Docker installed to run Testcontainers.

## Postman documentation
You can view request examples through this 
[Postman Documentation](https://documenter.getpostman.com/view/26801632/2s93zFXeZL).

## Additional Information
### A Few Words about Interfaces for Single Service Classes

I acknowledge that using an interface for every single class is considered bad practice.
However, using concrete classes instead of interfaces causes Spring to use Cglib instead of
the native API, which is considered the preferred approach in the Spring documentation.
Therefore, I have chosen to use interfaces even for a single class.
