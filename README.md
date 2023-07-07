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

### A Few Words About Lack Of Time

Attention! I found an error one hour before the deadline and 
I don't have time to fix it. Specifically, 
the contacts should be unique within each user, not the entire program. 
Unfortunately, due to significant time constraints, 
I was rushing and didn't take this design detail into account.

Please note that due to the limited timeframe of 3 days for completing this test task, 
I implemented some features in a functional but suboptimal manner. 
Given more time, I would have utilized a more robust and efficient approach. 
I wanted to ensure the core functionality within the given deadline.
