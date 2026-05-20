describe('Admin Critical Path', () => {
  // Clear cookies and local storage before each test to ensure a clean state
  beforeEach(() => {
    cy.clearCookies();
    cy.clearLocalStorage();
  });

  it('should allow admin to login, add product, and check orders', () => {
    // 1. Login
    cy.log('Step 1: Admin Login');
    cy.visit('/login');
    
    // Check if we are on the login page
    cy.contains('登录账户').should('be.visible');
    
    // Click the "Admin Login" link
    cy.contains('管理员登录').click();
    
    // Verify we are redirected to admin dashboard
    // Note: Since the current frontend implementation might not have real auth protection,
    // we verify the URL and the presence of admin layout elements.
    cy.url().should('include', '/admin');
    cy.contains('优选冰箱').should('be.visible');
    cy.contains('管理后台').should('be.visible');
    
    // 2. Product Add
    cy.log('Step 2: Add New Product');
    // Navigate to Product Management page
    cy.contains('商品管理').click();
    cy.url().should('include', '/admin/products');
    
    // Click "Add Product" button
    cy.contains('button', '添加商品').click();
    
    // Verify Modal is open
    cy.contains('添加商品').should('be.visible');
    
    // Fill in the product form
    // Using placeholders as selectors based on the React component code
    const timestamp = new Date().getTime();
    const productName = `Test Fridge ${timestamp}`;
    
    cy.get('input[placeholder="请输入商品名称"]').type(productName);
    cy.get('input[placeholder="请输入品牌"]').type('CypressTestBrand');
    cy.get('input[placeholder="请输入售价"]').type('5999');
    cy.get('input[placeholder="请输入库存"]').type('50');
    cy.get('input[placeholder="例如：500L"]').type('500L');
    
    // Select category (if select exists)
    cy.get('select').select('智能冰箱');
    
    // Fill description
    cy.get('textarea[placeholder="请输入商品描述..."]').type('This is a test product added by Cypress E2E test.');
    
    // Submit the form
    cy.contains('button', '确认添加').click();
    
    // Verify the product is added to the list
    // Since the mock data is local state, it should appear immediately
    cy.contains(productName).should('be.visible');
    cy.contains('CypressTestBrand').should('be.visible');
    
    // 3. Order Check
    cy.log('Step 3: Check Orders');
    // Navigate to Order Management page
    cy.contains('订单管理').click();
    
    // Verify URL and Page Title
    cy.url().should('include', '/admin/orders');
    // Based on sidebar badges, there might be '3' orders in mock data
    // We just verify the page loaded successfully
    cy.get('table').should('exist'); // Assuming there is a table for orders
  });
});
