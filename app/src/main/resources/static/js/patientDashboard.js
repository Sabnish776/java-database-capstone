// patientDashboard.js
import { getDoctors } from './services/doctorServices.js';
import { openModal } from './components/modals.js';
import { createDoctorCard } from './components/doctorCard.js';
import { filterDoctors } from './services/doctorServices.js';//call the same function to avoid duplication coz the functionality was same
import { patientSignup, patientLogin } from './services/patientServices.js';



document.addEventListener("DOMContentLoaded", () => {
  loadDoctorCards();
});

document.addEventListener("DOMContentLoaded", () => {

    const btn = document.getElementById("openLoginBtn");
    if (btn) {
        btn.addEventListener("click", () => openModal("patientLogin"));
    }
});
document.addEventListener("DOMContentLoaded", () => {

    const btn = document.getElementById("openSignupBtn");
    if (btn) {
        btn.addEventListener("click", () => openModal("patientSignup"));
    }
});

function loadDoctorCards() {
  getDoctors()
    .then(response => {
        const doctors = response.doctors ;
      const contentDiv = document.getElementById("content");
      contentDiv.innerHTML = "";

      doctors.forEach(doctor => {
        const card = createDoctorCard(doctor);
        contentDiv.appendChild(card);
      });
    })
    .catch(error => {
      console.error("Failed to load doctors:", error);
    });
}
// Filter Input
const searchBar = document.getElementById("searchBar");
const filterTime = document.getElementById("filterTime");
const filterSpecialty = document.getElementById("filterSpecialty");

if (searchBar) {
    searchBar.addEventListener("input", filterDoctorsOnChange);
}
if (filterTime) {
    filterTime.addEventListener("change", filterDoctorsOnChange);
}
if (filterSpecialty) {
    filterSpecialty.addEventListener("change", filterDoctorsOnChange);
}

// function filterDoctorsOnChange() {
//   const searchBar = document.getElementById("searchBar").value.trim();
//   const filterTime = document.getElementById("filterTime").value;
//   const filterSpecialty = document.getElementById("filterSpecialty").value;
//
//
//   const name = searchBar.length > 0 ? searchBar : null;
//   const time = filterTime.length > 0 ? filterTime : null;
//   const specialty = filterSpecialty.length > 0 ? filterSpecialty : null;
//
//   filterDoctors(name, time, specialty)
//     .then(response => {
//       const doctors = response.doctors;
//       const contentDiv = document.getElementById("content");
//       contentDiv.innerHTML = "";
//
//       if (doctors.length > 0) {
//         console.log(doctors);
//         doctors.forEach(doctor => {
//           const card = createDoctorCard(doctor);
//           contentDiv.appendChild(card);
//         });
//       } else {
//         contentDiv.innerHTML = "<p>No doctors found with the given filters.</p>";
//         console.log("Nothing");
//       }
//     })
//     .catch(error => {
//       console.error("Failed to filter doctors:", error);
//       alert("❌ An error occurred while filtering doctors.");
//     });
// }

async function filterDoctorsOnChange() {

    const name = document.getElementById("searchBar")?.value || "";
    const time = document.getElementById("filterTime")?.value || "";
    const specialty = document.getElementById("filterSpecialty")?.value || "";

    const doctors = await filterDoctors(name, time, specialty);

    const contentDiv = document.getElementById("content");
    contentDiv.innerHTML = "";

    if (!doctors || doctors.length === 0) {
        contentDiv.innerHTML = "<p>No doctors found</p>";
        return;
    }

    renderDoctorCards(doctors);
}

function renderDoctorCards(doctors) {
    console.log("rendering doctor cards\n") ;
    const contentDiv = document.getElementById("content");
    contentDiv.innerHTML = "";

    doctors.forEach((doctor) => {
        const card = createDoctorCard(doctor);
        contentDiv.appendChild(card);
    });
}

window.signupPatient = async function () {
  try {
      console.log("patient signup triggerred") ;
    const name = document.getElementById("name").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const phone = document.getElementById("phone").value;
    const address = document.getElementById("address").value;

    const data = { name, email, password, phone, address };
    const { success, message } = await patientSignup(data);
    if (success) {
      alert(message);
      document.getElementById("modal").style.display = "none";
      window.location.reload();
    }
    else alert(message);
  } catch (error) {
    console.error("Signup failed:", error);
    alert("❌ An error occurred while signing up.");
  }
};

window.loginPatient = async function () {
  try {
      console.log("patient login triggerred") ;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const data = {
      email,
      password
    }
    console.log("loginPatient :: ", data)
    const response = await patientLogin(data);
    console.log("Status Code:", response.status);
    console.log("Response OK:", response.ok);
    if (response.ok) {
      const result = await response.json();
      console.log(result);
      selectRole('loggedPatient');
      localStorage.setItem('token', result.token)
      window.location.href = '/pages/loggedPatientDashboard.html';
    } else {
      alert('❌ Invalid credentials!');
    }
  }
  catch (error) {
    alert("❌ Failed to Login : ", error);
    console.log("Error :: loginPatient :: ", error)
  }


}
