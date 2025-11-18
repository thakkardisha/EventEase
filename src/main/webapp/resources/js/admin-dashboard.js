//// The BASE_URL for your JAX-RS resource
//const BASE_URL = '/api/admin'; 
//
//// Table names derived from your REST class imports
//const tableNames = [
//    'Events', 'Venues', 'Categories', 'Coupons', 'Bookings', 
//    // Add others: 'Payments', 'Reviews', 'Artists', 'EventImages', 'Wishlists'
//];
//
//let currentTable = 'Events'; // Global state for current CRUD table
//
//// --- Security and Utility Functions ---
//
///**
// * Handles clearing the token and redirecting the user upon 401/403 errors.
// */
//function clearTokenAndRedirect() {
//    console.warn("JWT expired or unauthorized access detected. Redirecting to login.");
//    localStorage.removeItem('jwtToken');
//    // Assuming your login page is at the root
//    window.location.href = '/login.xhtml'; 
//}
//
///**
// * Generic fetch wrapper that includes JWT Authorization header.
// */
//function fetchData(url, options = {}) {
//    const token = localStorage.getItem('jwtToken');
//    
//    // Set headers, including the token if present
//    const defaultHeaders = {
//        'Content-Type': 'application/json'
//    };
//    if (token) {
//        defaultHeaders['Authorization'] = `Bearer ${token}`; 
//    }
//    
//    // Merge headers from options with default headers
//    const mergedOptions = {
//        ...options,
//        headers: {
//            ...defaultHeaders,
//            ...options.headers // Allow overriding/adding custom headers
//        }
//    };
//
//    return fetch(url, mergedOptions)
//        .then(response => {
//            if (response.status === 401 || response.status === 403) {
//                // Unauthorized or Forbidden access: Assume token is expired or user lacks role
//                clearTokenAndRedirect();
//                return null; // Stop processing
//            }
//            if (!response.ok) {
//                return response.text().then(text => { throw new Error(text || `HTTP error! status: ${response.status}`); });
//            }
//            // Handle 204 No Content for DELETE/UPDATE if applicable
//            if (response.status === 204) return null; 
//            
//            return response.json();
//        })
//        .catch(error => {
//            console.error("Fetch error:", error);
//            return null;
//        });
//}
//
//
//// --- CRUD Management (loadTableData) ---
//
//// loadTableData function updated to use the JWT-aware fetchData
//async function loadTableData(tableName) {
//    const container = $('#crud-table-view');
//    container.html('<p style="text-align:center;">Loading ' + tableName + ' data...</p>');
//
//    let endpoint = '';
//    // Use the GET REST methods you provided
//    switch (tableName) {
//        case 'Events': endpoint = '/events/getAllEvents'; break;
//        case 'Venues': endpoint = '/venues/getAllVenues'; break;
//        case 'Categories': endpoint = '/category/getAllCategories'; break;
//        case 'Coupons': endpoint = '/coupons/getAllCoupons/'; break;
//        default: 
//            container.html('<p class="error-text">No list endpoint defined for ' + tableName + '.</p>');
//            return;
//    }
//
//    // Use JWT-aware fetchData
//    const data = await fetchData(BASE_URL + endpoint);
//    
//    // ... (rest of loadTableData function remains the same, assuming it generates the table HTML) ...
//    // [The HTML generation logic from previous answer should be here]
//    if (data && data.length > 0) {
//        const keys = Object.keys(data[0]).slice(0, 5); // Show first 5 columns
//        let tableHtml = `<table class="data-table"><thead><tr>`;
//        keys.forEach(key => { tableHtml += `<th>${key.toUpperCase()}</th>`; });
//        tableHtml += `<th>Actions</th></tr></thead><tbody>`;
//
//        data.forEach(item => {
//            tableHtml += `<tr>`;
//            keys.forEach(key => { tableHtml += `<td>${item[key] || '-'}</td>`; });
//            const idKey = keys[0]; 
//            const id = item[idKey]; 
//            const itemJson = JSON.stringify(item).replace(/"/g, '&quot;');
//            
//            tableHtml += `
//                <td>
//                    <button type="button" class="action-btn" title="Edit" onclick="openEditModal('${tableName}', ${id}, '${itemJson}')"><i class="fas fa-edit"></i></button>
//                    <button type="button" class="action-btn delete-btn" title="Delete" onclick="deleteRecord('${tableName}', ${id})"><i class="fas fa-trash"></i></button>
//                </td>
//            </tr>`;
//        });
//
//        tableHtml += `</tbody></table>`;
//        container.html(tableHtml);
//    } else if (data !== null) { // Only show this if data is empty, not if fetch failed (which returns null)
//        container.html('<p style="text-align:center;">No ' + tableName + ' records found.</p>');
//    }
//}
//
//
//// --- CRUD Submission (handleCrudSubmit) ---
//
//// handleCrudSubmit function updated to use the JWT-aware fetch call
//async function handleCrudSubmit(e) {
//    e.preventDefault();
//    const operation = $('#crud-operation').val();
//    const errorText = $('#crud-error-text');
//    errorText.text('Saving...').css('color', '#B7FFB7');
//
//    let path = '';
//    let dataBody = null;
//    let method = '';
//    
//    // 1. Get the JWT token for the request
//    const token = localStorage.getItem('jwtToken');
//    if (!token) {
//        clearTokenAndRedirect();
//        return;
//    }
//
//    try {
//        // ... (Path and dataBody preparation logic from previous answer) ...
//        // [Ensure switch cases for path/dataBody/method assignment are here]
//
//        // --- Start Path & Data Body Logic ---
//        if (operation === 'CREATE') {
//            method = 'POST';
//            switch (currentTable) {
//                case 'Events':
//                    const eventParams = [
//                        $('#eName').val(), $('#description').val(), $('#eventDate').val(), 
//                        $('#startTime').val(), $('#endTime').val(), $('#unitPrice').val(), 
//                        $('#vId').val(), $('#cId').val(), $('#maxCapacity').val(), 
//                        $('#bannerImg').val(), $('#status').val()
//                    ];
//                    path = `/event/create/${eventParams.join('/')}`;
//                    break;
//                case 'Venues':
//                    const venueParams = [
//                        $('#vName').val(), $('#vAddress').val(), $('#vCity').val(), 
//                        $('#vState').val(), $('#vCapacity').val()
//                    ];
//                    path = `/venue/addvenue/${venueParams.join('/')}`;
//                    break;
//                // ... (Other CREATE cases) ...
//                case 'Categories':
//                    const categoryParams = [
//                        $('#cName').val(), $('#cDescription').val(), $('#cImg').val()
//                    ];
//                    path = `/category/addcategory/${categoryParams.join('/')}`;
//                    break;
//                case 'Coupons':
//                    const couponParams = [
//                        $('#cCode').val(), $('#discountType').val(), $('#discountValue').val(), 
//                        $('#maxUses').val(), $('#validFrom').val(), $('#validTo').val(), 
//                        $('#status').val(), $('#isSingleUse').val()
//                    ];
//                    path = `/coupons/createcoupon/${couponParams.join('/')}`;
//                    break;
//                default:
//                    throw new Error(`Create not implemented for ${currentTable}.`);
//            }
//        } else if (operation === 'UPDATE') {
//            method = 'PUT';
//            const id = $('#record-id').val();
//            
//            switch (currentTable) {
//                case 'Events':
//                    path = `/event/${id}`;
//                    dataBody = JSON.stringify({
//                        eId: parseInt(id),
//                        eName: $('#eName').val(),
//                        description: $('#description').val(),
//                        unitPrice: parseFloat($('#unitPrice').val()),
//                        maxCapacity: parseInt($('#maxCapacity').val()),
//                        status: $('#status').val(),
//                        venues: { vId: parseInt($('#vId').val()) },
//                        categories: { cId: parseInt($('#cId').val()) }
//                    });
//                    break;
//                case 'Venues':
//                    path = `/venue`; 
//                    dataBody = JSON.stringify({
//                        vId: parseInt(id),
//                        vName: $('#vName').val(),
//                        vAddress: $('#vAddress').val(),
//                        vCity: $('#vCity').val(),
//                        vState: $('#vState').val(),
//                        vCapacity: parseInt($('#vCapacity').val())
//                    });
//                    break;
//                // ... (Other UPDATE cases) ...
//                case 'Categories':
//                    path = `/category/${id}`;
//                    dataBody = JSON.stringify({
//                        cId: parseInt(id),
//                        cName: $('#cName').val(),
//                        cDescription: $('#cDescription').val(),
//                        cImg: $('#cImg').val()
//                    });
//                    break;
//                case 'Coupons':
//                    path = `/coupons/${id}`;
//                    dataBody = JSON.stringify({
//                        cId: parseInt(id),
//                        cCode: $('#cCode').val(),
//                        discountType: $('#discountType').val(),
//                        discountValue: parseInt($('#discountValue').val()),
//                        maxUses: parseInt($('#maxUses').val()),
//                        validFrom: $('#validFrom').val(),
//                        validTo: $('#validTo').val(),
//                        status: $('#status').val(),
//                        isSingleUse: JSON.parse($('#isSingleUse').val())
//                    });
//                    break;
//                default:
//                    throw new Error(`Update not implemented for ${currentTable}.`);
//            }
//        }
//        // --- End Path & Data Body Logic ---
//
//        const response = await fetch(BASE_URL + path, { 
//            method: method,
//            headers: { 
//                'Content-Type': 'application/json',
//                'Authorization': `Bearer ${token}` // Include the JWT here
//            },
//            body: dataBody
//        });
//        
//        // Handle 401/403 errors separately to redirect
//        if (response.status === 401 || response.status === 403) {
//             clearTokenAndRedirect();
//             return;
//        }
//
//        if (response.ok || response.status === 201) {
//            errorText.text(`${currentTable.slice(0, -1)} ${operation.toLowerCase()}d successfully!`);
//            setTimeout(() => {
//                closeModal();
//                loadTableData(currentTable); 
//            }, 1500);
//        } else {
//            const errorBody = await response.text();
//            throw new Error(errorBody || `${operation} failed.`);
//        }
//    } catch (error) {
//        errorText.css('color', '#FF8A8A');
//        const errorMessage = error.message.split('Error : ')[1] || error.message;
//        errorText.text('Error: ' + errorMessage.replace(/"/g, ''));
//    }
//}
//
//
//// --- Delete Record ---
//
//// deleteRecord function updated to use the JWT-aware fetch call
//async function deleteRecord(tableName, id) {
//    if (!confirm(`Are you sure you want to delete ${tableName.slice(0, -1)} with ID ${id}?`)) return;
//
//    let path = '';
//    switch (tableName) {
//        case 'Events': path = `/event/${id}`; break;
//        case 'Venues': path = `/venue/${id}`; break;
//        case 'Categories': path = `/category/${id}`; break;
//        case 'Coupons': path = `/coupons/${id}`; break;
//        default:
//            alert(`Delete not implemented for ${tableName}.`);
//            return;
//    }
//    
//    // 1. Get the JWT token for the request
//    const token = localStorage.getItem('jwtToken');
//    if (!token) {
//        clearTokenAndRedirect();
//        return;
//    }
//
//    try {
//        const response = await fetch(BASE_URL + path, { 
//            method: 'DELETE',
//            headers: { 'Authorization': `Bearer ${token}` }
//        });
//        
//        // Handle 401/403 errors separately to redirect
//        if (response.status === 401 || response.status === 403) {
//             clearTokenAndRedirect();
//             return;
//        }
//
//        if (response.ok || response.status === 204) {
//            alert(`${tableName.slice(0, -1)} deleted successfully.`);
//            loadTableData(tableName);
//        } else {
//            const errorBody = await response.text();
//            throw new Error(errorBody || 'Deletion failed.');
//        }
//    } catch (error) {
//        alert('Error deleting record: ' + error.message);
//    }
//}
//
//
//// --- Remaining functions (openCreateModal, openEditModal, renderUserReport, etc.) ---
//// These remain largely the same, as they handle UI rendering, not the fetch logic itself.
//
//// Initialization logic remains the same (calls are now JWT-aware):
//$(document).ready(function() {
//    // Check for token on load. If missing, redirect.
//    if (!localStorage.getItem('jwtToken')) {
//        clearTokenAndRedirect();
//    } else {
//        // Load the initial section
//        // Note: The UI:include in the xhtml handles the initial load
//        // But we run a specific section function if it needs client-side data fetching
//        // Example: loadTableData(currentTable); 
//    }
//});