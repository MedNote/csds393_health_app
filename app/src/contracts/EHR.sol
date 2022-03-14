pragma solidity >=0.7.0 <0.9.0;

contract EHR{

    /// test data
    address admin = 0x5B38Da6a701c568545dCfcB03FcB875f56beddC4;

    struct Patient{
        string firstName;
        string lastName;
        /// public key of patient
        bytes32 pubKey;
        /// doctors authorized to read/write this patient's EHR
        address[] authorized;
        bool valid;
    }

    struct Doctor{
        string firstName;
        string lastName;
        /// public key of doctor
        bytes32 pubKey;
        bool valid;
    }

    struct MedicalRecord{
        /// address of the doctor who created this record
        address doctorAddress;
        /// hash of latest file version
        bytes32 hash;
        bool valid;
    }
    
    /// associate patient's wallet address with Patient struct
    mapping (address => Patient) patients;
    /// associate doctot's wallet address with Doctor struct
    mapping (address => Doctor) doctors;
    /// associate patient's wallet address with doctor address
    mapping (address => address) patientProviders;
    /// associate patient's wallet address with MedicalRecord
    mapping (address => MedicalRecord) records;
    

    constructor() {                 
         require(msg.sender == admin);
    }

    function createPatient(
        string memory _firstName, 
        string memory _lastName, 
        address _patientAddress, 
        bytes32 _patientPublicKey
    ) 
        public 
    {
        /// only allow doctors to create patients, and patient can't already exist
        require(doctors[msg.sender].valid && !patients[_patientAddress].valid);
        patients[_patientAddress].firstName = _firstName;
        patients[_patientAddress].lastName = _lastName;
        patients[_patientAddress].pubKey = _patientPublicKey;
        patients[_patientAddress].authorized = [msg.sender];
        patients[_patientAddress].valid = true;
        records[_patientAddress].doctorAddress = msg.sender;
        /// hash of null data
        records[_patientAddress].hash = 0xe3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855;
    }

    function createDoctor(
        string memory _firstName, 
        string memory _lastName, 
        address _doctorAddress, 
        bytes32 _doctorPublicKey
    ) 
        public
    {
        /// only admin can create doctor, and doctor must not already exist
        require(msg.sender == admin && !doctors[_doctorAddress].valid);
        doctors[_doctorAddress].firstName = _firstName;
        doctors[_doctorAddress].lastName = _lastName;
        doctors[_doctorAddress].pubKey = _doctorPublicKey;
        doctors[_doctorAddress].valid = true;
    }

    function authorizeDoctor(address _doctorAddress) public
    {
        /// doctor should exist and patient should exist and doctor shouldn't already have authorization
        require(doctors[_doctorAddress].valid 
                && patients[msg.sender].valid
                && contains(patients[msg.sender].authorized, _doctorAddress) == -1);
        patients[msg.sender].authorized.push(_doctorAddress);
    }

    function deauthorizeDoctor(address _doctorAddress) public
    {
        /// doctor should exist and patient should exist and doctor should have authorization
        int idx = contains(patients[msg.sender].authorized, _doctorAddress);
        require(doctors[_doctorAddress].valid 
                && patients[msg.sender].valid 
                && idx != -1);
        patients[msg.sender].authorized[uint(idx)] = patients[msg.sender].authorized[patients[msg.sender].authorized.length-1];
        patients[msg.sender].authorized.pop();
    }

    /// Check if address is in list
    /// @param list the list of addresses to check
    /// @param add the address to search for
    /// @return index of address in list if found and -1 otherwise
    function contains(address[] memory list, address add) internal returns (int)
    {
        for (uint i = 0; i < list.length; i++) {
            if (list[i] == add) {
                return int(i);
            }
        }
        return int(-1);
    }

}