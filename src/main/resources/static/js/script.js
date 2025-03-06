document.addEventListener("DOMContentLoaded", () => {
  const addContactBtn = document.getElementById("addContactBtn");
  const contactModal = document.getElementById("contactModal");
  const closeModal = document.getElementById("closeModal");
  const cancelBtn = document.getElementById("cancelBtn");
  const contactForm = document.getElementById("contactForm");
  const deleteButtons = document.querySelectorAll(".delete-contact");
  const editButtons = document.querySelectorAll(".edit-contact");
  const deleteModal = document.getElementById("deleteModal");
  const closeDeleteModal = document.getElementById("closeDeleteModal");
  const cancelDeleteBtn = document.getElementById("cancelDeleteBtn");
  const confirmDeleteBtn = document.getElementById("confirmDeleteBtn");
  const detailsModal = document.getElementById("detailsModal");
  const closeDetailsModal = document.getElementById("closeDetailsModal");
  const editFromDetailsBtn = document.getElementById("editFromDetailsBtn");
  const deleteFromDetailsBtn = document.getElementById("deleteFromDetailsBtn");

  // Open Add/Edit Modal
  function openModal(isEdit = false, person = null) {
    contactModal.classList.add("active");
    document.getElementById("modalTitle").textContent = isEdit
      ? "Edit Contact"
      : "Add New Contact";
    if (isEdit && person) {
      document.getElementById("resourceName").value = person.resourceName || "";
      document.getElementById("givenName").value =
        person.names && person.names[0] ? person.names[0].givenName || "" : "";
      document.getElementById("familyName").value =
        person.names && person.names[0] ? person.names[0].familyName || "" : "";
      document.getElementById("phoneValue").value =
        person.phoneNumbers && person.phoneNumbers[0]
          ? person.phoneNumbers[0].value || ""
          : "";
    } else {
      contactForm.reset();
      document.getElementById("resourceName").value = "";
    }
  }

  // Close Add/Edit Modal
  function closeModalFunc() {
    contactModal.classList.remove("active");
  }

  // Open Delete Modal
  function openDeleteModal(resourceName) {
    deleteModal.classList.add("active");
    confirmDeleteBtn.setAttribute("data-id", resourceName);
  }

  // Close Delete Modal
  function closeDeleteModalFunc() {
    deleteModal.classList.remove("active");
  }

  // Open Details Modal
  function openDetailsModal(contactItem) {
    const resourceName = contactItem.getAttribute("data-id");
    const fullName = contactItem
      .querySelector(".contact-name")
      .textContent.trim();
    const phone = contactItem
      .querySelector(".contact-phone")
      .textContent.trim();

    document.getElementById("detailsInitials").textContent = fullName
      .split(" ")
      .map((n) => n[0])
      .join("");
    document.getElementById("detailsName").textContent = fullName;
    document.getElementById("detailsPhone").textContent = phone;

    // Set data attributes for edit and delete
    editFromDetailsBtn.setAttribute("data-id", resourceName);
    deleteFromDetailsBtn.setAttribute("data-id", resourceName);

    detailsModal.classList.add("active");
  }

  // Close Details Modal
  function closeDetailsModalFunc() {
    detailsModal.classList.remove("active");
  }

  // Event Listeners for Modals
  if (addContactBtn) {
    addContactBtn.addEventListener("click", () => openModal(false));
  }

  if (closeModal) {
    closeModal.addEventListener("click", closeModalFunc);
  }

  if (cancelBtn) {
    cancelBtn.addEventListener("click", closeModalFunc);
  }

  if (closeDeleteModal) {
    closeDeleteModal.addEventListener("click", closeDeleteModalFunc);
  }

  if (cancelDeleteBtn) {
    cancelDeleteBtn.addEventListener("click", closeDeleteModalFunc);
  }

  if (closeDetailsModal) {
    closeDetailsModal.addEventListener("click", closeDetailsModalFunc);
  }

  // Handle form submission for create/update
  if (contactForm) {
    contactForm.addEventListener("submit", (e) => {
      e.preventDefault();
      const formData = new FormData(contactForm);
      const person = {
        resourceName: formData.get("resourceName"),
        names: [
          {
            givenName: formData.get("names[0].givenName"),
            familyName: formData.get("names[0].familyName"),
          },
        ],
        phoneNumbers: [
          {
            value: formData.get("phoneNumbers[0].value"),
          },
        ],
      };

      if (!person.resourceName) {
        // Create new contact (POST)
        fetch("/api/contacts", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(person),
        })
          .then((response) => {
            if (!response.ok) throw new Error("Network response was not ok");
            return response.json();
          })
          .then((data) => {
            alert("Contact saved successfully!");
            window.location.reload();
          })
          .catch((error) => {
            console.error("Error saving contact:", error);
            alert("Error saving contact");
          });
      } else {
        // Update existing contact (PUT)
        fetch(`/api/contacts/${person.resourceName}`, {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(person),
        })
          .then((response) => {
            if (!response.ok) throw new Error("Network response was not ok");
            return response.json();
          })
          .then((data) => {
            alert("Contact updated successfully!");
            window.location.reload();
          })
          .catch((error) => {
            console.error("Error updating contact:", error);
            alert("Error updating contact");
          });
      }
      closeModalFunc();
    });
  }

  // Handle delete button clicks
  deleteButtons.forEach((button) => {
    button.addEventListener("click", function (e) {
      e.stopPropagation();
      const resourceName = this.getAttribute("data-id");
      openDeleteModal(resourceName);
    });
  });

  // Handle edit button clicks by extracting contact info from the DOM
  editButtons.forEach((button) => {
    button.addEventListener("click", function (e) {
      e.stopPropagation();
      const contactItem = this.closest(".contact-item");
      const resourceName = contactItem.getAttribute("data-id");
      const fullName = contactItem
        .querySelector(".contact-name")
        .textContent.trim();
      const nameParts = fullName.split(" ");
      const givenName = nameParts[0] || "";
      const familyName = nameParts[1] || "";
      const phone = contactItem
        .querySelector(".contact-phone")
        .textContent.trim();

      const person = {
        resourceName: resourceName,
        names: [{ givenName: givenName, familyName: familyName }],
        phoneNumbers: [{ value: phone }],
      };
      openModal(true, person);
    });
  });

  // Handle confirm delete
  if (confirmDeleteBtn) {
    confirmDeleteBtn.addEventListener("click", function () {
      const resourceName = this.getAttribute("data-id");
      fetch(`/api/contacts/${resourceName}`, {
        method: "DELETE",
      })
        .then((response) => {
          if (!response.ok) throw new Error("Network response was not ok");
          alert("Contact deleted successfully!");
          window.location.reload();
        })
        .catch((error) => {
          console.error("Error deleting contact:", error);
          alert("Error deleting contact");
        });
      closeDeleteModalFunc();
      closeDetailsModalFunc();
    });
  }

  // Handle contact item clicks to open details modal
  const contactItems = document.querySelectorAll(".contact-item");
  contactItems.forEach((item) => {
    item.addEventListener("click", function (e) {
      if (!e.target.closest("button")) {
        openDetailsModal(this);
      }
    });
  });

  // Handle edit from details modal
  if (editFromDetailsBtn) {
    editFromDetailsBtn.addEventListener("click", function () {
      const resourceName = this.getAttribute("data-id");
      const contactItem = document.querySelector(
        `.contact-item[data-id="${resourceName}"]`
      );
      if (!contactItem) return;
      const fullName = contactItem
        .querySelector(".contact-name")
        .textContent.trim();
      const nameParts = fullName.split(" ");
      const givenName = nameParts[0] || "";
      const familyName = nameParts[1] || "";
      const phone = contactItem
        .querySelector(".contact-phone")
        .textContent.trim();
      closeDetailsModalFunc();
      const person = {
        resourceName: resourceName,
        names: [{ givenName: givenName, familyName: familyName }],
        phoneNumbers: [{ value: phone }],
      };
      openModal(true, person);
    });
  }

  // Handle delete from details modal
  if (deleteFromDetailsBtn) {
    deleteFromDetailsBtn.addEventListener("click", function () {
      const resourceName = this.getAttribute("data-id");
      closeDetailsModalFunc();
      openDeleteModal(resourceName);
    });
  }
});
