# Final Project Description

Create a store application. Design your API in a RESTful manner. In the examples, you get samples of JSONs that your API should receive or send.

Your store has to support the following methods:

### 1. Register new user
* **Example request:** `{"email": "my@email.com", "password": "123"}`
* **Response:** Respond with appropriate HTTP codes (200 for ok, 409 for existing user).
* **Requirement:** Your app must not store password as plain text, use some good approach to identify user.

### 2. Login into system
* **Example request:** `{"email": "my@email.com", "password": "123"}`
* **Response:** Respond with JSON containing `sessionId`.
* **Optional:** Think about preventing an intruder from bruteforcing.

### 3. Reset password (Optional)

### 4. Get all products in store
* **Response:** Respond with JSON list of items you have.
* **Example:**
    ```json
    {
      "id": "2411", 
      "title": "Nail gun", 
      "available": 8, 
      "price": "23.95"
    }
    ```

### 5. Add item to cart
* **Example request:** `{"id": "363", "quantity": "2"}`
* **Logic:** Allow adding only one position at a time. If you don’t have this quantity in store - respond with an error.
* **Session Scope:** The information has to be session-scoped: once session expires - user will get new empty cart.

### 6. Display your cart content
* **Response:** Respond with list of product names with their quantities added. Calculate subtotal. Assign an ordinal to each cart item.

### 7. Remove an item from user’s cart

### 8. Modify cart item
* **Example request:** `{"id": 2, "quantity": 3}`
* **Logic:** User should be able to modify number of some items in his cart.

### 9. Checkout
* **Logic:** Verify your prices in cart, ensure you still have desired amount of goods. If all is good - send a user confirmation about successful order.

### 10. Cancel order (Optional)
* **Logic:** Return all products from order back to available status.

### 11. Get user’s order list (Optional)
* **Response:** Should contain order id, date, total, status.