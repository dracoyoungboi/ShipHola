// Form Validation Module
const FormValidator = {
    // Vietnamese validation rules
    rules: {
        required: (value) => value && value.trim().length > 0,
        email: (value) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value),
        phone: (value) => /^[0-9]{10}$/.test(value),
        minLength: (value, min) => value && value.length >= min,
        maxLength: (value, max) => value && value.length <= max,
        min: (value, min) => value && parseFloat(value) >= min,
        max: (value, max) => value && parseFloat(value) <= max,
        numeric: (value) => !isNaN(parseFloat(value)) && isFinite(value)
    },

    // Error messages in Vietnamese
    messages: {
        required: 'Trường này không được để trống',
        email: 'Email không hợp lệ',
        phone: 'Số điện thoại phải là 10 chữ số',
        minLength: 'Giá trị quá ngắn',
        maxLength: 'Giá trị quá dài',
        min: 'Giá trị quá nhỏ',
        max: 'Giá trị quá lớn',
        numeric: 'Phải là số'
    },

    validate(form) {
        const errors = {};
        const formData = new FormData(form);

        for (const [name, rules] of Object.entries(form.dataset)) {
            if (name.startsWith('rule-')) {
                const fieldName = name.replace('rule-', '');
                const fieldRules = JSON.parse(rules);
                const value = formData.get(fieldName);

                for (const [rule, ruleValue] of Object.entries(fieldRules)) {
                    if (this.rules[rule]) {
                        const isValid = Array.isArray(ruleValue)
                            ? this.rules[rule](value, ...ruleValue)
                            : this.rules[rule](value, ruleValue);

                        if (!isValid) {
                            if (!errors[fieldName]) {
                                errors[fieldName] = [];
                            }
                            errors[fieldName].push(
                                this.messages[rule] || `Invalid ${rule}`
                            );
                        }
                    }
                }
            }
        }

        return {
            isValid: Object.keys(errors).length === 0,
            errors
        };
    },

    showError(field, message) {
        const formGroup = field.closest('.form-group');
        let errorElement = formGroup.querySelector('.field-error');

        if (!errorElement) {
            errorElement = document.createElement('div');
            errorElement.className = 'field-error';
            formGroup.appendChild(errorElement);
        }

        errorElement.textContent = message;
        errorElement.style.display = 'block';
        field.classList.add('error');
    },

    clearError(field) {
        const formGroup = field.closest('.form-group');
        const errorElement = formGroup.querySelector('.field-error');

        if (errorElement) {
            errorElement.style.display = 'none';
        }
        field.classList.remove('error');
    }
};

// Auto-initialize forms with validation
document.addEventListener('DOMContentLoaded', () => {
    const forms = document.querySelectorAll('form[data-auto-validate]');

    forms.forEach(form => {
        form.addEventListener('submit', (e) => {
            const result = FormValidator.validate(form);

            if (!result.isValid) {
                e.preventDefault();

                // Clear all errors first
                form.querySelectorAll('.field-error').forEach(el => el.remove());
                form.querySelectorAll('.error').forEach(el => el.classList.remove('error'));

                // Show new errors
                for (const [field, messages] of Object.entries(result.errors)) {
                    const fieldElement = form.querySelector(`[name="${field}"]`);
                    if (fieldElement) {
                        FormValidator.showError(fieldElement, messages[0]);
                    }
                }
            }
        });

        // Clear errors on input
        form.querySelectorAll('input, textarea, select').forEach(field => {
            field.addEventListener('input', () => {
                FormValidator.clearError(field);
            });
        });
    });
});

window.FormValidator = FormValidator;
