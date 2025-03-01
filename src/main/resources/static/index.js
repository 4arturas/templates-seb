angular.module('templateApp', ['ui.bootstrap'])
    .controller('TemplateController', ['$scope', '$http', function($scope, $http) {
        $scope.templates = [];
        $scope.loading = true;
        $scope.currentTemplateData = {};
        $scope.modalTitle = "";
        $scope.modalMessage = "";
        $scope.modalConfirmAction = null;
        $scope.templateContent = ""; // Store the fetched template content

        // Fetch all templates
        $scope.getTemplates = function() {
            $scope.loading = true;
            $http.get('/api/templates')
                .then(function(response) {
                    $scope.templates = response.data;
                    $scope.loading = false;
                }, function(error) {
                    console.error("Error fetching templates:", error);
                    $scope.loading = false;
                    handleErrorResponse(error);
                });
        };

        $scope.getTemplates();

        // Show confirmation modal for deletion
        $scope.showConfirmation = function(templateId) {
            $scope.modalTitle = "Confirm Deletion";
            $scope.modalMessage = "Are you sure you want to delete this template?";
            $scope.modalConfirmAction = function() {
                $scope.confirmDelete(templateId);
            };
            $('#confirmationModal').modal('show');
        };

        // Confirm deletion
        $scope.confirmDelete = function(templateId) {
            $http.delete('/api/templates/' + templateId)
                .then(function() {
                    $scope.getTemplates();
                    $('#confirmationModal').modal('hide');
                }, function(error) {
                    console.error("Error deleting template:", error);
                    $('#confirmationModal').modal('hide');
                    handleErrorResponse(error);
                });
        };

        // Show edit/upload modal
        $scope.editTemplate = function(template) {
            $scope.modalTitle = "Edit Template";
            $scope.currentTemplateData = angular.copy(template); // Pre-fill data for editing
            $('#actionModal').modal('show');
        };

        $scope.showUploadNew = function() {
            $scope.modalTitle = "Upload New Template";
            $scope.currentTemplateData = {}; // Clear data for new upload
            $('#actionModal').modal('show');
        };

        // Confirm action (edit or upload)
        $scope.confirmAction = function() {
            if (!$scope.currentTemplateData.name) {
                alert("Please provide a template name.");
                return;
            }

            var fileInput = document.getElementById('templateFile');
            var file = fileInput.files[0];
            var data = {
                name: $scope.currentTemplateData.name,
                userId: $scope.currentTemplateData.userId || 1, // Default user_id
                tribeId: $scope.currentTemplateData.tribeId || 1 // Default tribe_id
            };

            if (file) {
                var reader = new FileReader();
                reader.onload = function(event) {
                    data.content = event.target.result;

                    if ($scope.currentTemplateData.id) { // Editing an existing template
                        $http.put('/api/templates/' + $scope.currentTemplateData.id, data)
                            .then(function() {
                                $scope.getTemplates();
                                $('#actionModal').modal('hide');
                            }, function(error) {
                                handleErrorResponse(error);
                            });
                    } else { // Uploading a new template
                        $http.post('/api/templates', data)
                            .then(function() {
                                $scope.getTemplates();
                                $('#actionModal').modal('hide');
                            }, function(error) {
                                handleErrorResponse(error);
                            });
                    }
                };

                reader.onerror = function() {
                    alert("Error reading file.");
                };

                reader.readAsText(file);
            } else {
                if ($scope.currentTemplateData.id) { // Editing without a file update
                    $http.put('/api/templates/' + $scope.currentTemplateData.id, data)
                        .then(function() {
                            $scope.getTemplates();
                            $('#actionModal').modal('hide');
                        }, function(error) {
                            handleErrorResponse(error);
                        });
                } else {
                    alert("Please select a file for upload.");
                }
            }
        };

        // Cancel action
        $scope.cancelAction = function() {
            $('#actionModal').modal('hide');
            $scope.currentTemplateData = {}; // Reset data
            fileInput.value = ''; // Clear file input
        };

        // Modal confirm action
        $scope.modalConfirm = function() {
            if ($scope.modalConfirmAction) {
                $scope.modalConfirmAction(); // Execute the confirm action
            }
        };

        // View template content
        $scope.viewContent = function(templateId) {
            $http.get('/api/templates/' + templateId)
                .then(function(response) {
                    $scope.templateContent = response.data.content; // Fetch and set the content
                    $('#contentModal').modal('show'); // Show the content modal
                }, function(error) {
                    handleErrorResponse(error);
                });
        };

        // Centralized error handling function
        function handleErrorResponse(error) {
            let errorMessage = "An unknown error occurred.";
            if (error && error.data) {
                errorMessage = error.data.message || error.data.error || error.data; // Try different properties
            } else if (error && error.statusText) {
                errorMessage = error.statusText; // Fallback to statusText
            } else if (error && error.status) {
                errorMessage = "HTTP Error " + error.status; // Fallback to HTTP status
            }
            alert("Error: " + errorMessage);
        }
    }]);