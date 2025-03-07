// DOM Elements
const contactsGrid = document.getElementById("contacts-grid");
const contactCount = document.getElementById("contact-count");
const addContactBtn = document.getElementById("add-contact-btn");

// Modals
const contactModal = document.getElementById("contact-modal");
const deleteModal = document.getElementById("delete-modal");
const successModal = document.getElementById("success-modal");
const detailsModal = document.getElementById("details-modal");

// Forms
const contactForm = document.getElementById("contact-form");
const deleteForm = document.getElementById("delete-form");

// Form elements
const modalTitle = document.getElementById("modal-title");
const resourceNameInput = document.getElementById("resource-name");
const contactFnameInput = document.getElementById("contact-fname");
const contactLnameInput = document.getElementById("contact-lname");
const contactEmailContainer = document.getElementById("email-container");
const contactPhoneContainer = document.getElementById("phone-container");
const contactBirthdayInput = document.getElementById("contact-birthday");
const contactNotesInput = document.getElementById("contact-notes");

// Details modal elements
const detailsName = document.getElementById("details-name");
const detailsEmailsList = document.getElementById("details-emails-list");
const detailsPhonesList = document.getElementById("details-phones-list");
const detailsBirthdayValue = document.getElementById("details-birthday-value");
const detailsNotesValue = document.getElementById("details-notes-value");

// Buttons
const cancelBtn = document.getElementById("cancel-btn");
const cancelDeleteBtn = document.getElementById("cancel-delete-btn");
const confirmDeleteBtn = document.getElementById("confirm-delete-btn");
const successOkBtn = document.getElementById("success-ok-btn");
const addEmailBtn = document.getElementById("add-email-btn");
const addPhoneBtn = document.getElementById("add-phone-btn");

// Other elements
const successMessage = document.getElementById("success-message");
const closeButtons = document.querySelectorAll(".close-modal");

// Contact data
let currentContactId = null;

// Initialize
function init() {
  console.log("Initializing application...");

  // Add contact button
  if (addContactBtn) {
    console.log("Setting up add contact button");
    addContactBtn.addEventListener("click", function (e) {
      e.preventDefault();
      openAddModal();
    });
  }

  // Contact cards click
  setupCardClickListeners();

  // Form submission - prevent default and handle with JS
  if (contactForm) {
    contactForm.addEventListener("submit", function (e) {
      e.preventDefault();
      handleFormSubmit();
    });
  }

  // Delete form submission - prevent default and handle with JS
  if (deleteForm) {
    deleteForm.addEventListener("submit", function (e) {
      e.preventDefault();
      handleDeleteSubmit();
    });
  }

  // Cancel buttons
  if (cancelBtn) {
    cancelBtn.addEventListener("click", function () {
      closeModal(contactModal);
    });
  }

  if (cancelDeleteBtn) {
    cancelDeleteBtn.addEventListener("click", function () {
      closeModal(deleteModal);
    });
  }

  // Confirm delete button
  if (confirmDeleteBtn) {
    confirmDeleteBtn.addEventListener("click", function (e) {
      e.preventDefault();
      handleDeleteSubmit();
    });
  }

  // Success OK button
  if (successOkBtn) {
    successOkBtn.addEventListener("click", function () {
      closeModal(successModal);
    });
  }

  // Close modal buttons
  closeButtons.forEach((button) => {
    button.addEventListener("click", function () {
      const modal = this.closest(".modal");
      closeModal(modal);
    });
  });

  // Dynamic field addition for emails and phones
  if (addEmailBtn) {
    addEmailBtn.addEventListener("click", function () {
      addEmailField();
    });
  }

  if (addPhoneBtn) {
    addPhoneBtn.addEventListener("click", function () {
      addPhoneField();
    });
  }

  // Close modal when clicking outside
  setupModalOutsideClickListeners();
}

// Setup card click listeners
function setupCardClickListeners() {
  const cards = document.querySelectorAll(".contact-card");
  console.log(`Found ${cards.length} contact cards`);

  cards.forEach((card) => {
    card.addEventListener("click", function (e) {
      // Don't open details if clicking on action buttons
      if (!e.target.closest(".action-btn")) {
        console.log("Card clicked, opening details modal");
        const resName = this.getAttribute("data-resource-name") || "";
        const fName = this.getAttribute("data-contact-fname") || "";
        const lName = this.getAttribute("data-contact-lname") || "";
        const emails = this.getAttribute("data-contact-emails") || "";
        const phones = this.getAttribute("data-contact-phones") || "";
        const birthday = this.getAttribute("data-contact-birthday") || "";
        const notes = this.getAttribute("data-contact-notes") || "";

        openDetailsModal(
          resName,
          fName,
          lName,
          emails,
          phones,
          birthday,
          notes
        );
      }
    });
  });

  // Edit buttons
  document.querySelectorAll(".edit-btn").forEach((btn) => {
    btn.addEventListener("click", function (e) {
      e.stopPropagation(); // Prevent card click
      console.log("Edit button clicked");
      const resName = this.getAttribute("data-resource-name") || "";
      const fName = this.getAttribute("data-contact-fname") || "";
      const lName = this.getAttribute("data-contact-lname") || "";
      const emails = this.getAttribute("data-contact-emails") || "";
      const phones = this.getAttribute("data-contact-phones") || "";
      const birthday = this.getAttribute("data-contact-birthday") || "";
      const notes = this.getAttribute("data-contact-notes") || "";

      openEditModal(resName, fName, lName, emails, phones, birthday, notes);
    });
  });

  // Delete buttons
  document.querySelectorAll(".delete-btn").forEach((btn) => {
    btn.addEventListener("click", function (e) {
      e.stopPropagation(); // Prevent card click
      console.log("Delete button clicked");
      const resName = this.getAttribute("data-resource-name");
      openDeleteModal(resName);
    });
  });
}

// Setup modal outside click listeners
function setupModalOutsideClickListeners() {
  const modals = document.querySelectorAll(".modal");

  modals.forEach((modal) => {
    modal.addEventListener("click", function (e) {
      // Close if clicking outside the modal content
      if (e.target === modal) {
        closeModal(modal);
      }
    });
  });
}

// Open details modal
function openDetailsModal(
  resName,
  fName,
  lName,
  emails,
  phones,
  birthday,
  notes
) {
  console.log("Opening details modal");

  // Set name in header
  const fullName = `${fName} ${lName}`.trim();
  detailsName.textContent = fullName || "Contact Details";

  // Set emails
  detailsEmailsList.innerHTML = "";
  detailsEmailsList.className = "details-list emails";
  if (emails) {
    emails.split(/,\s*/).forEach((email) => {
      const li = document.createElement("li");
      li.textContent = email.trim();
      detailsEmailsList.appendChild(li);
    });
    document.getElementById("details-emails").style.display = "block";
  } else {
    document.getElementById("details-emails").style.display = "none";
  }

  // Set phones
  detailsPhonesList.innerHTML = "";
  detailsPhonesList.className = "details-list phones";
  if (phones) {
    phones.split(/,\s*/).forEach((phone) => {
      const li = document.createElement("li");
      li.textContent = phone.trim();
      detailsPhonesList.appendChild(li);
    });
    document.getElementById("details-phones").style.display = "block";
  } else {
    document.getElementById("details-phones").style.display = "none";
  }

  // Set birthday
  if (birthday) {
    detailsBirthdayValue.textContent = birthday;
    document.getElementById("details-birthday").style.display = "block";
  } else {
    document.getElementById("details-birthday").style.display = "none";
  }

  // Set notes
  if (notes) {
    detailsNotesValue.textContent = notes;
    document.getElementById("details-notes").style.display = "block";
  } else {
    document.getElementById("details-notes").style.display = "none";
  }

  openModal(detailsModal);
}

// Open add contact modal
function openAddModal() {
  console.log("Opening add contact modal");
  modalTitle.textContent = "Add Contact";
  resourceNameInput.value = "";
  contactFnameInput.value = "";
  contactLnameInput.value = "";
  contactBirthdayInput.value = "";
  contactNotesInput.value = "";
  clearContainer(contactEmailContainer);
  clearContainer(contactPhoneContainer);

  // Add one default email and phone field
  addEmailField();
  addPhoneField();

  // For creation, POST /contacts
  contactForm.action = "/contacts";
  openModal(contactModal);
}

// Open edit contact modal
function openEditModal(resName, fName, lName, emails, phones, birthday, notes) {
  console.log("Opening edit contact modal");
  modalTitle.textContent = "Edit Contact";
  resourceNameInput.value = resName;
  contactFnameInput.value = fName;
  contactLnameInput.value = lName;
  contactBirthdayInput.value = birthday;
  contactNotesInput.value = notes;
  clearContainer(contactEmailContainer);
  clearContainer(contactPhoneContainer);

  // Prepopulate email and phone containers with the provided values
  if (emails) {
    emails.split(/,\s*/).forEach((email) => {
      addEmailField(email.trim());
    });
  } else {
    addEmailField();
  }

  if (phones) {
    phones.split(/,\s*/).forEach((phone) => {
      addPhoneField(phone.trim());
    });
  } else {
    addPhoneField();
  }

  // Use path: /contacts/{resourceName}/update
  contactForm.action = "/contacts/" + resName + "/update";
  openModal(contactModal);
}

// Open delete confirmation modal
function openDeleteModal(resName) {
  console.log("Opening delete modal");
  currentContactId = resName;
  deleteForm.action = "/contacts/" + resName + "/delete";
  openModal(deleteModal);
}

// Dynamic addition of an email input field
function addEmailField(value = "") {
  const input = document.createElement("input");
  input.type = "email";
  input.className = "contact-email-input";
  input.name = "email";
  input.value = value;
  input.style.display = "block";
  input.style.marginBottom = "8px";
  input.placeholder = "Email address";
  contactEmailContainer.appendChild(input);
}

// Dynamic addition of a phone input field
function addPhoneField(value = "") {
  const input = document.createElement("input");
  input.type = "tel";
  input.className = "contact-phone-input";
  input.name = "phone";
  input.value = value;
  input.style.display = "block";
  input.style.marginBottom = "8px";
  input.placeholder = "Phone number";
  contactPhoneContainer.appendChild(input);
}

// Helper to clear a container's children
function clearContainer(container) {
  while (container && container.firstChild) {
    container.removeChild(container.firstChild);
  }
}

// Handle form submission
function handleFormSubmit() {
  console.log("Handling form submission");
  // Get form data
  const formData = new FormData(contactForm);
  const url = contactForm.action;

  // Send form data via fetch
  fetch(url, {
    method: "POST",
    body: formData,
  })
    .then((response) => {
      if (response.ok) {
        closeModal(contactModal);
        showSuccessMessage(
          modalTitle.textContent === "Add Contact"
            ? "Contact added successfully!"
            : "Contact updated successfully!"
        );

        // Reload page after a delay to show the success message
        setTimeout(() => {
          window.location.reload();
        }, 1500);
      } else {
        throw new Error("Form submission failed");
      }
    })
    .catch((error) => {
      console.error("Error:", error);
      alert("An error occurred. Please try again.");
    });
}

// Handle delete submission
function handleDeleteSubmit() {
  console.log("Handling delete submission");
  const url = deleteForm.action;

  fetch(url, {
    method: "POST",
  })
    .then((response) => {
      if (response.ok) {
        closeModal(deleteModal);
        showSuccessMessage("Contact deleted successfully!");

        // Reload page after a delay to show the success message
        setTimeout(() => {
          window.location.reload();
        }, 1500);
      } else {
        throw new Error("Delete failed");
      }
    })
    .catch((error) => {
      console.error("Error:", error);
      alert("An error occurred. Please try again.");
    });
}

// Modal functions
function openModal(modal) {
  console.log("Opening modal");
  if (modal) {
    modal.style.display = "flex";
  } else {
    console.error("Modal not found");
  }
}

function closeModal(modal) {
  console.log("Closing modal");
  if (modal) {
    modal.style.display = "none";
  } else {
    console.error("Modal not found");
  }
}

// Show success message
function showSuccessMessage(message) {
  console.log("Showing success message:", message);
  if (successMessage) {
    successMessage.textContent = message;
  }
  openModal(successModal);
}

// Initialize the app
document.addEventListener("DOMContentLoaded", init);
