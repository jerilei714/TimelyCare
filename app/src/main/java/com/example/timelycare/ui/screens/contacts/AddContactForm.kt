package com.example.timelycare.ui.screens.contacts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import com.example.timelycare.data.CountryCode
import com.example.timelycare.data.CountryCodes
import com.example.timelycare.data.EmergencyContact
import com.example.timelycare.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactForm(
    onAddContact: (EmergencyContact) -> Unit,
    onSelectFromPhonebook: () -> Unit,
    isPhoneNumberExists: (String) -> Boolean,
    editingContact: EmergencyContact? = null,
    onCancelEdit: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var name by remember(editingContact) { mutableStateOf(editingContact?.name ?: "") }
    var selectedCountryCode by remember(editingContact) {
        mutableStateOf(
            if (editingContact != null) {
                CountryCodes.all.find { it.code == editingContact.countryCode } ?: CountryCodes.PHILIPPINES
            } else {
                CountryCodes.PHILIPPINES
            }
        )
    }
    var phone by remember(editingContact, selectedCountryCode) {
        mutableStateOf(
            editingContact?.phone?.let { sanitizePhoneNumberInput(it, selectedCountryCode) } ?: ""
        )
    }

    var nameError by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf("") }

    fun validateForm(): Boolean {
        var isValid = true

        nameError = when {
            name.isBlank() -> {
                isValid = false
                "Contact name is required"
            }
            name.length < 2 -> {
                isValid = false
                "Name must be at least 2 characters"
            }
            else -> ""
        }

        phoneError = when {
            phone.isBlank() -> {
                isValid = false
                "Phone number is required"
            }
            phone.length < 7 -> {
                isValid = false
                "Phone number must be at least 7 digits"
            }
            !phone.all { it.isDigit() } -> {
                isValid = false
                "Phone number must contain only digits"
            }
            isPhoneNumberExists(phone) -> {
                isValid = false
                "This phone number already exists"
            }
            else -> ""
        }

        return isValid
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TimelyCareWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (editingContact != null) "Edit Contact" else "Add Contact",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TimelyCareTextPrimary
                )

                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Contact form",
                    tint = TimelyCareBlue,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Contact name field
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = ""
                },
                label = { Text("Contact name *") },
                isError = nameError.isNotEmpty(),
                supportingText = if (nameError.isNotEmpty()) {
                    { Text(nameError, color = MaterialTheme.colorScheme.error) }
                } else null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TimelyCareBlue,
                    unfocusedBorderColor = TimelyCareGray,
                    focusedTextColor = TimelyCareTextPrimary,
                    unfocusedTextColor = TimelyCareTextPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // Country code dropdown
            CountryCodeDropdown(
                selectedCountryCode = selectedCountryCode,
                onCountryCodeSelected = {
                    selectedCountryCode = it
                    phone = sanitizePhoneNumberInput(phone, it)
                }
            )

            // Phone number field
            OutlinedTextField(
                value = phone,
                onValueChange = {
                    phone = sanitizePhoneNumberInput(it, selectedCountryCode)
                    phoneError = ""
                },
                label = { Text("Phone number *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = phoneError.isNotEmpty(),
                supportingText = if (phoneError.isNotEmpty()) {
                    { Text(phoneError, color = MaterialTheme.colorScheme.error) }
                } else null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TimelyCareBlue,
                    unfocusedBorderColor = TimelyCareGray,
                    focusedTextColor = TimelyCareTextPrimary,
                    unfocusedTextColor = TimelyCareTextPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // Action buttons
            if (editingContact != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onCancelEdit,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (validateForm()) {
                                onAddContact(
                                    editingContact.copy(
                                        name = name.trim(),
                                        phone = phone,
                                        countryCode = selectedCountryCode.code
                                    )
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TimelyCareBlue
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Update Contact",
                            color = TimelyCareWhite,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (validateForm()) {
                                onAddContact(
                                    EmergencyContact(
                                        name = name.trim(),
                                        phone = phone,
                                        countryCode = selectedCountryCode.code
                                    )
                                )
                                name = ""
                                phone = ""
                                selectedCountryCode = CountryCodes.PHILIPPINES
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TimelyCareBlue
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Add Contact",
                            color = TimelyCareWhite,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(4.dp)
                        )
                    }

                    OutlinedButton(
                        onClick = onSelectFromPhonebook,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Select from Phonebook",
                            color = TimelyCareBlue,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun sanitizePhoneNumberInput(rawInput: String, countryCode: CountryCode): String {
    val digitsOnly = rawInput.filter { it.isDigit() }
    if (countryCode.code == CountryCodes.PHILIPPINES.code && digitsOnly.length > 1 && digitsOnly.first() == '0') {
        return digitsOnly.drop(1)
    }
    return digitsOnly
}