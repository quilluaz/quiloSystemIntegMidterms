// DOM references
const addContactBtn = document.getElementById("add-contact-btn");
const contactModal = document.getElementById("contact-modal");
const contactForm = document.getElementById("contact-form");
const modalTitle = document.getElementById("modal-title");
const resourceNameInput = document.getElementById("resource-name");

// We now have separate firstName & lastName fields
const contactFnameInput = document.getElementById("contact-fname");
const contactLnameInput = document.getElementById("contact-lname");
const contactEmailInput = document.getElementById("contact-email");
const contactNumberInput = document.getElementById("contact-number");
const profilePicPreview = document.getElementById("profile-pic-preview");

const deleteModal = document.getElementById("delete-modal");
const deleteForm = document.getElementById("delete-form");
const deleteResourceInput = document.getElementById("delete-resource");

const successModal = document.getElementById("success-modal");
const successMessage = document.getElementById("success-message");

// Buttons
const cancelBtn = document.getElementById("cancel-btn");
const cancelDeleteBtn = document.getElementById("cancel-delete-btn");
const successOkBtn = document.getElementById("success-ok-btn");

// Close icons on modals
const closeButtons = document.querySelectorAll(".close-modal");

function init() {
  // "Add Contact" button opens the add modal
  if (addContactBtn) {
    addContactBtn.addEventListener("click", openAddModal);
  }

  // Cancel in the add/edit modal
  cancelBtn.addEventListener("click", () => closeModal(contactModal));

  // Cancel in the delete modal
  cancelDeleteBtn.addEventListener("click", () => closeModal(deleteModal));

  // Dismiss success modal
  successOkBtn.addEventListener("click", () => closeModal(successModal));

  // X icons in modals
  closeButtons.forEach((button) => {
    button.addEventListener("click", function () {
      const modal = this.closest(".modal");
      closeModal(modal);
    });
  });

  // Attach edit event to each "edit-btn"
  document.querySelectorAll(".edit-btn").forEach((btn) => {
    btn.addEventListener("click", () => {
      const resName = btn.getAttribute("data-resource-name") || "";
      const fName = btn.getAttribute("data-contact-fname") || "";
      const lName = btn.getAttribute("data-contact-lname") || "";
      const cEmail = btn.getAttribute("data-contact-email") || "";
      const cPhone = btn.getAttribute("data-contact-phone") || "";

      openEditModal(resName, fName, lName, cEmail, cPhone);
    });
  });

  // Attach delete event to each "delete-btn"
  document.querySelectorAll(".delete-btn").forEach((btn) => {
    btn.addEventListener("click", () => {
      const resName = btn.getAttribute("data-resource-name");
      openDeleteModal(resName);
    });
  });
}

// "Add Contact" form
function openAddModal() {
  modalTitle.textContent = "Add Contact";
  resourceNameInput.value = "";
  contactFnameInput.value = "";
  contactLnameInput.value = "";
  contactEmailInput.value = "";
  contactNumberInput.value = "";

  profilePicPreview.innerHTML =
    '<span class="material-icons">account_circle</span>';

  // For creation, POST /contacts
  contactForm.action = "/contacts";

  openModal(contactModal);
}

// "Edit Contact" form
function openEditModal(resName, fName, lName, cEmail, cPhone) {
  modalTitle.textContent = "Edit Contact";
  resourceNameInput.value = resName;
  contactFnameInput.value = fName;
  contactLnameInput.value = lName;
  contactEmailInput.value = cEmail;
  contactNumberInput.value = cPhone;

  profilePicPreview.innerHTML =
    '<span class="material-icons">account_circle</span>';

  // Use path: /contacts/{resourceName}/update
  contactForm.action = "/contacts/" + resName + "/update";

  openModal(contactModal);
}

// "Delete Contact" form
function openDeleteModal(resName) {
  deleteForm.action = "/contacts/" + resName + "/delete";
  openModal(deleteModal);
}

// Optional success message
function showSuccessMessage(msg) {
  successMessage.textContent = msg;
  openModal(successModal);
}

// Open/Close helpers
function openModal(modal) {
  modal.style.display = "flex";
}
function closeModal(modal) {
  modal.style.display = "none";
}

document.addEventListener("DOMContentLoaded", init);
