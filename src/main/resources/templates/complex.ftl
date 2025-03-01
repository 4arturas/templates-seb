<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title}</title>
</head>
<body>

<h1>${heading}</h1>

<!-- Display a message -->
<p>${message}</p>

<!-- Check if the user is logged in -->
<#if isLoggedIn??>
    <p>Welcome back, ${user.name}!</p>
<#else>
    <p>Please log in to continue.</p>
</#if>

<!-- Iterate over an array of items -->
<h2>Items List</h2>
<ul>
    <#list items as item>
        <li>
            Name: ${item.name}<br>
            Price: $${item.price}<br>
            <#if item.inStock>
                <span style="color:green;">In Stock</span>
            <#else>
                <span style="color:red;">Out of Stock</span>
            </#if>
        </li>
    </#list>
</ul>

<!-- Switch-like logic using #list -->
<h2>Item Categories</h2>
<ul>
    <#list items as item>
        <li>
            Item: ${item.name} - Category:
            <#switch item.category>
                <#case "electronics">
                    Electronics
                    <#break>
                <#case "books">
                    Books
                    <#break>
                <#case "clothing">
                    Clothing
                    <#break>
                <#default>
                    Unknown
            </#switch>
        </li>
    </#list>
</ul>

<!-- Display a table of user data -->
<h2>User Data</h2>
<table border="1">
    <thead>
    <tr>
        <th>Name</th>
        <th>Email</th>
        <th>Role</th>
    </tr>
    </thead>
    <tbody>
    <#list users as user>
        <tr>
            <td>${user.name}</td>
            <td>${user.email}</td>
            <td>
                <#if user.role == "admin">
                    Administrator
                <#elseif user.role == "editor">
                    Editor
                <#else>
                    User
                </#if>
            </td>
        </tr>
    </#list>
    </tbody>
</table>


</body>
</html>