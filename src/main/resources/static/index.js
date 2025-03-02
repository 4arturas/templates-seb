angular.module('templateApp', ['ui.bootstrap'])
    .controller('TemplateController', ['$scope', '$http', function($scope, $http) {
        $scope.templates = [];
        $scope.filteredTemplates = [];
        $scope.loading = true;
        $scope.currentTemplateData = {};
        $scope.modalTitle = "";
        $scope.modalMessage = "";
        $scope.modalConfirmAction = null;
        $scope.searchQuery = "";

        $scope.listOfColumns = [
            {
                name: 'ID',
                sortOrder: null,
                sortFn: (a, b) => a.id - b.id,
                sortDirections: ['ascend', 'descend', null],
                filterMultiple: false,
                listOfFilter: [],
                filterFn: null
            },
            {
                name: 'Name',
                sortOrder: null,
                sortFn: (a, b) => a.name.localeCompare(b.name),
                sortDirections: ['ascend', 'descend', null],
                filterMultiple: true,
                listOfFilter: [],
                filterFn: (list, item) => list.some(name => item.name.toLowerCase().includes(name.toLowerCase()))
            },
            {
                name: 'User ID',
                sortOrder: null,
                sortFn: (a, b) => a.userId - b.userId,
                sortDirections: ['ascend', 'descend', null],
                filterMultiple: true,
                listOfFilter: [],
                filterFn: (list, item) => list.some(userId => item.userId.toString() === userId)
            },
            {
                name: 'Tribe ID',
                sortOrder: null,
                sortFn: (a, b) => a.tribeId - b.tribeId,
                sortDirections: ['ascend', 'descend', null],
                filterMultiple: true,
                listOfFilter: [],
                filterFn: (list, item) => list.some(tribeId => item.tribeId.toString() === tribeId)
            },
            {
                name: 'Date Created',
                sortOrder: null,
                sortFn: (a, b) => new Date(a.dateCreated) - new Date(b.dateCreated),
                sortDirections: ['ascend', 'descend', null],
                filterMultiple: false,
                listOfFilter: [],
                filterFn: null
            }
        ];

        $scope.currentPage = 1;
        $scope.pageSize = 5;

        $scope.getTemplates = function() {
            $scope.loading = true;
            $http.get('/api/templates')
                .then(function(response) {
                    $scope.templates = response.data || [];
                    $scope.applyFiltersAndSorting();
                    $scope.totalPages = Math.ceil($scope.filteredTemplates.length / $scope.pageSize);
                    $scope.pages = Array.from({ length: $scope.totalPages }, (_, i) => i + 1);
                    $scope.loading = false;
                }, function(error) {
                    console.error("Error fetching templates:", error);
                    $scope.loading = false;
                    handleErrorResponse(error);
                });
        };

        $scope.getTemplates();

        $scope.applyFiltersAndSorting = function() {
            let filtered = $scope.templates;

            if ($scope.searchQuery) {
                const query = $scope.searchQuery.toLowerCase();
                filtered = filtered.filter(template =>
                    template.name.toLowerCase().includes(query) ||
                    template.userId.toString().includes(query) ||
                    template.tribeId.toString().includes(query) ||
                    (template.dateCreated && new Date(template.dateCreated).toLocaleDateString().includes(query))
                );
            }

            $scope.listOfColumns.forEach(column => {
                if (column.filterFn && column.listOfFilter.length > 0) {
                    const filterValues = column.listOfFilter.filter(f => f.checked).map(f => f.value);
                    if (filterValues.length > 0) {
                        filtered = filtered.filter(item => column.filterFn(filterValues, item));
                    }
                }
            });

            const sortColumn = $scope.listOfColumns.find(c => c.sortOrder === 'ascend' || c.sortOrder === 'descend');
            if (sortColumn && sortColumn.sortFn) {
                const sortOrder = sortColumn.sortOrder === 'ascend' ? 1 : -1;
                filtered.sort((a, b) => sortOrder * sortColumn.sortFn(a, b));
            }

            $scope.filteredTemplates = filtered;
            $scope.totalPages = Math.ceil($scope.filteredTemplates.length / $scope.pageSize);
            $scope.pages = Array.from({ length: $scope.totalPages }, (_, i) => i + 1);
            $scope.currentPage = 1; // Reset to first page after filter/sort
        };

        $scope.sort = function(column) {
            if (column.sortOrder === null) {
                column.sortOrder = 'ascend';
            } else if (column.sortOrder === 'ascend') {
                column.sortOrder = 'descend';
            } else {
                column.sortOrder = null;
            }

            $scope.listOfColumns.forEach(c => {
                if (c !== column) {
                    c.sortOrder = null;
                }
            });

            $scope.applyFiltersAndSorting();
        };

        $scope.setCurrentPage = function(page) {
            if (page >= 1 && page <= $scope.totalPages) {
                $scope.currentPage = page;
            }
        };

        $scope.filter = function(column, filterValue) {
            if (column.listOfFilter.find(f => f.value === filterValue)) {
                column.listOfFilter.find(f => f.value === filterValue).checked = !column.listOfFilter.find(f => f.value === filterValue).checked;
            } else {
                column.listOfFilter.push({ text: filterValue, value: filterValue, checked: true });
            }
            $scope.applyFiltersAndSorting();
        };

        $scope.showConfirmation = function(templateId) {
            $scope.modalTitle = "Confirm Deletion";
            $scope.modalMessage = "Are you sure you want to delete this template?";
            $scope.modalConfirmAction = function() {
                $scope.confirmDelete(templateId);
            };
            $('#confirmationModal').modal('show');
        };

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

        $scope.editTemplate = function(template) {
            $scope.modalTitle = "Edit Template";
            $scope.currentTemplateData = angular.copy(template);
            $('#actionModal').modal('show');
        };

        $scope.showUploadNew = function() {
            $scope.modalTitle = "Upload New Template";
            $scope.currentTemplateData = {};
            $('#actionModal').modal('show');
        };

        $scope.confirmAction = function() {
            if (!$scope.currentTemplateData.name) {
                alert("Please provide a template name.");
                return;
            }

            var fileInput = document.getElementById('templateFile');
            var file = fileInput.files[0];
            var data = {
                name: $scope.currentTemplateData.name,
                userId: $scope.currentTemplateData.userId || 1,
                tribeId: $scope.currentTemplateData.tribeId || 1
            };

            if (file) {
                var reader = new FileReader();
                reader.onload = function(event) {
                    data.content = event.target.result;

                    if ($scope.currentTemplateData.id) {
                        $http.put('/api/templates/' + $scope.currentTemplateData.id, data)
                            .then(function() {
                                $scope.getTemplates();
                                $('#actionModal').modal('hide');
                            }, function(error) {
                                handleErrorResponse(error);
                            });
                    } else {
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
                if ($scope.currentTemplateData.id) {
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

        $scope.cancelAction = function() {
            $('#actionModal').modal('hide');
            $scope.currentTemplateData = {};
            var fileInput = document.getElementById('templateFile');
            if (fileInput) {
                fileInput.value = '';
            }
        };

        $scope.modalConfirm = function() {
            if ($scope.modalConfirmAction) {
                $scope.modalConfirmAction();
            }
        };

        $scope.viewContent = function(templateId) {
            $http.get('/api/templates/' + templateId)
                .then(function(response) {
                    $scope.templateContent = response.data.content;
                    $('#contentModal').modal('show');
                }, function(error) {
                    handleErrorResponse(error);
                });
        };

        function handleErrorResponse(error) {
            let errorMessage = "An unknown error occurred.";
            if (error && error.data) {
                errorMessage = error.data.message || error.data.error || error.data;
            } else if (error && error.statusText) {
                errorMessage = error.statusText;
            } else if (error && error.status) {
                errorMessage = "HTTP Error " + error.status;
            }
            alert("Error: " + errorMessage);
        }
    }])
    .filter('startFrom', function() {
        return function(input, start) {
            if (!input || !input.length) return [];
            start = parseInt(start, 10);
            return input.slice(start);
        };
    });