document.addEventListener('DOMContentLoaded', function() {
    console.log("admin user");

    const fileInput = document.querySelector("#image_file_input");
    const imagePreview = document.querySelector("#upload_image_preview");

    fileInput.addEventListener("change", function(event) {
        const file = event.target.files[0];

        if (file) {
            // Validate file type
            if (!file.type.match('image.*')) {
                alert('Please select an image file (JPEG, PNG, etc.)');
                fileInput.value = '';
                return;
            }

            // Validate file size (e.g., 2MB max)
            if (file.size > 2 * 1024 * 1024) {
                alert('Image must be less than 2MB');
                fileInput.value = '';
                return;
            }

            const reader = new FileReader();

            reader.onload = function(e) {
                imagePreview.src = e.target.result;
                imagePreview.style.display = 'block'; // Show the preview
            };

            reader.readAsDataURL(file);
        } else {
            // If no file selected, show the existing image or placeholder
            const currentSrc = imagePreview.dataset.thSrc || 'https://via.placeholder.com/300';
            imagePreview.src = currentSrc;
        }
    });

    // Initialize with existing image if available
    if (imagePreview.dataset.thSrc) {
        imagePreview.src = imagePreview.dataset.thSrc;
        imagePreview.style.display = 'block';
    }
});