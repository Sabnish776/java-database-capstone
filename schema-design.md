## Mysql database design

**Patients table**

    id : int , primary key , auto increment 
    Name : varchar , not null
    email : varchar
    password : varchar (hashed pass)
    age : int 
    gender : varchar
    phone : varchar
    ROLE : varchar (default -> patient)

**Doctors table**

    id : int , primary key , auto increment
    name : varchar , not null
    email : varchar
    password : varchar (hashed pass)
    specialization : varchar
    age : int
    phone : varchar
    ROLE : varchar (doctor)

**Appointments**

    id : int , primary key , auto increment
    patientID : foreign key -> Patients(id)
    doctorID : foreign key -> Doctors(id)
    appointmentDate : DATETIME , not null
    duration : INT (hours)
    status : int , 0 for scheduled , 1 for completed , 2 for cancelled

**Admin**

    id : int , primary key , auto increment
    username : varchar , not null
    password : varchar (hashed pass)
    email : varchar
    ROLE : varchar (admin)
    created_at : DATETIME
    
## MongoDB database design
### Collection: prescriptions
```json
{
  "_id": "ObjectId('64abc123456')",
  "patientName": "John Smith",
  "appointmentId": 51,
  "medication": "Paracetamol",
  "dosage": "500mg",
  "doctorNotes": "Take 1 tablet every 6 hours.",
  "refillCount": 2,
  "pharmacy": {
    "name": "Walgreens SF",
    "location": "Market Street"
  }
}
    


    