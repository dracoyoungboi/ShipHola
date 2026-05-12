/**
 * Public Landing Page - ShipHola Theme
 * Handles 2-step order form, AJAX calls, geolocation, and scroll animations
 */

(function() {
  'use strict';

  // ============================================
  // CONFIGURATION
  // ============================================
  const CONFIG = {
    apiBaseUrl: '',
    geolocationTimeout: 30000, // 30 seconds for geolocation
    defaultPickupAddress: '',
    // Vietnamese address validation regex
    // Format: Số nhà, Tên đường, Phường/Xã, Quận/Huyện, Thành phố
    addressPatterns: [
      /\d+[\s\/-]*[đĐ]?/, // Phải có số nhà
      /(đường|đ|Đường|Đ|alley|ngõ|ngách|hẻm|hp|phố|ph)/i, // Phải có kiểu đường
      /(phường|xã|p\.|x\.|phường|xã|P\.|X\.)/i, // Phường/Xã (optional)
      /(quận|huyện|q\.|h\.|thành phố|tp\.|tph\.)/i // Quận/Huyện/Thành phố
    ]
  };

  // ============================================
  // STATE MANAGEMENT
  // ============================================
  const state = {
    currentStep: 1,
    feeCalculation: null, // Store calculated fee from Step 1
    currentPosition: null  // Store geolocation result
  };

  // ============================================
  // DOM ELEMENTS
  // ============================================
  const elements = {
    // Step 1 elements
    step1: null,
    serviceType: null,
    pickupAddress: null,
    deliveryAddress: null,
    surcharge: null,
    btnCalculateFee: null,
    btnGetLocation: null,

    // Step 2 elements
    step2: null,
    loadingState: null,
    successState: null,
    resultDistance: null,
    resultDuration: null,
    resultFee: null,
    resultTotal: null,
    senderPhone: null,
    receiverPhone: null,
    note: null,
    btnBack: null,
    btnConfirmOrder: null,

    // Success elements
    trackingNumber: null
  };

  // ============================================
  // UTILITY FUNCTIONS
  // ============================================
  function formatCurrency(amount) {
    if (amount === null || amount === undefined) return 'Thỏa thuận';
    return amount.toLocaleString('vi-VN') + ' đ';
  }

  /**
   * Validate địa chỉ theo format Việt Nam
   * @param address Địa chỉ cần validate
   * @returns {valid: boolean, message: string}
   */
  function validateVietnameseAddress(address) {
    if (!address || address.trim().length < 10) {
      return { valid: false, message: 'Địa chỉ quá ngắn. Vui lòng nhập đầy đủ thông tin.' };
    }

    const addr = address.trim().toLowerCase();

    // Check 1: Phải có số nhà (bắt đầu bằng số hoặc có số trong đó)
    if (!/\d/.test(addr)) {
      return { valid: false, message: 'Địa chỉ phải có số nhà.' };
    }

    // Check 2: Phải có tên đường hoặc từ khóa chỉ vị trí
    const hasStreet = /(đường|đ|alley|ngõ|ngách|hẻm|hp|phố|ph|xóm|ấp|thôn|ku|tổ|tớp)/i.test(addr);
    if (!hasStreet) {
      return { valid: false, message: 'Địa chỉ phải có tên đường hoặc từ khóa (ngõ, hẻm, ngách...).' };
    }

    // Check 3: Độ dài tối thiểu (địa chỉ đầy đủ thường > 20 ký tự)
    if (addr.length < 15) {
      return { valid: false, message: 'Địa chỉ chưa đầy đủ. Vui lòng nhập thêm phường/xã, quận/huyện.' };
    }

    return { valid: true, message: '' };
  }

  /**
   * Format lại địa chỉ cho đẹp
   * Ví dụ: "123 nguyen trai, p. ben nghe, q.1" -> "123 Nguyễn Trãi, Phường Bến Nghé, Quận 1"
   */
  function formatAddressDisplay(address) {
    return address
      .replace(/,/g, ', ')
      .replace(/\s+/g, ' ')
      .trim();
  }

  function showElement(el) {
    if (el) el.style.display = 'block';
  }

  function hideElement(el) {
    if (el) el.style.display = 'none';
  }

  function setButtonLoading(btn, loading, originalText) {
    if (!btn) return;
    if (loading) {
      // Store original content (only if not already stored)
      if (!btn.dataset.originalContent) {
        btn.dataset.originalContent = btn.innerHTML;
      }
      // Check if button has an icon (SVG)
      const hasIcon = btn.querySelector('svg');
      if (hasIcon) {
        // For buttons with icons, just add loading class and disable
        btn.classList.add('loading');
        btn.disabled = true;
        // Add spinning animation to icon
        const svg = btn.querySelector('svg');
        if (svg) {
          svg.style.animation = 'spin 1s linear infinite';
        }
      } else {
        // For text-only buttons, replace content
        btn.textContent = 'Đang xử lý...';
        btn.disabled = true;
      }
    } else {
      const hasIcon = btn.querySelector('svg');
      if (hasIcon) {
        // Restore original content
        btn.classList.remove('loading');
        btn.disabled = false;
        const svg = btn.querySelector('svg');
        if (svg) {
          svg.style.animation = '';
        }
      } else {
        btn.textContent = originalText || btn.dataset.originalContent || 'Xác nhận';
        btn.disabled = false;
      }
    }
  }

  // ============================================
  // GEOLOCATION
  // ============================================
  function getCurrentPosition() {
    return new Promise((resolve, reject) => {
      if (!navigator.geolocation) {
        reject(new Error('Trình duyệt không hỗ trợ định vị.'));
        return;
      }

      // First try: High accuracy (GPS) - shorter timeout
      const tryHighAccuracy = () => {
        return new Promise((resolveHigh, rejectHigh) => {
          navigator.geolocation.getCurrentPosition(
            (position) => resolveHigh({
              lat: position.coords.latitude,
              lon: position.coords.longitude
            }),
            (error) => rejectHigh(error),
            {
              enableHighAccuracy: true,
              timeout: 10000, // 10 seconds for high accuracy
              maximumAge: 0
            }
          );
        });
      };

      // Second try: Low accuracy (IP/WiFi triangulation) - longer timeout
      const tryLowAccuracy = () => {
        return new Promise((resolveLow, rejectLow) => {
          navigator.geolocation.getCurrentPosition(
            (position) => resolveLow({
              lat: position.coords.latitude,
              lon: position.coords.longitude
            }),
            (error) => rejectLow(error),
            {
              enableHighAccuracy: false, // Don't require GPS
              timeout: 15000, // 15 seconds for low accuracy
              maximumAge: 300000 // Accept cached position up to 5 minutes old
            }
          );
        });
      };

      // Try high accuracy first, fall back to low accuracy
      tryHighAccuracy()
        .then(resolve)
        .catch((highError) => {
          console.log('High accuracy failed, trying low accuracy...', highError);
          return tryLowAccuracy();
        })
        .then(resolve)
        .catch((lowError) => {
          let message = 'Không thể lấy vị trí.';
          switch (lowError.code) {
            case lowError.PERMISSION_DENIED:
              message = 'Bạn đã từ chối quyền định vị. Vui lòng cho phép truy cập vị trí trong cài đặt trình duyệt.';
              break;
            case lowError.POSITION_UNAVAILABLE:
              message = 'Thông tin vị trí không khả dụng.';
              break;
            case lowError.TIMEOUT:
              message = 'Hết thời gian định vị. Desktop có thể không có GPS. Vui lòng nhập địa chỉ thủ công.';
              break;
          }
          reject(new Error(message));
        });
    });
  }

  async function handleGetLocation() {
    const btn = elements.btnGetLocation;

    if (!btn) {
      return;
    }

    setButtonLoading(btn, true);

    try {
      const position = await getCurrentPosition();
      state.currentPosition = position;

      // Call server to reverse geocode
      const geocodeUrl = `${CONFIG.apiBaseUrl}/public/reverse-geocode?lat=${position.lat}&lon=${position.lon}`;
      const response = await fetch(geocodeUrl);

      if (!response.ok) {
        throw new Error('Không thể lấy địa chỉ từ tọa độ.');
      }

      const address = await response.text();

      if (address && address !== 'null' && address.length > 10) {
        // Parse the returned address and format it nicely for Vietnam
        // Nominatim returns: "123, Nguyễn Trãi, Phường Bến Nghé, Quận 1, Thành phố Hồ Chí Minh, Việt Nam"
        let formattedAddress = address;

        // Remove country name if present
        formattedAddress = formattedAddress.replace(/,\s*Việt\s*Nam$/i, '');
        formattedAddress = formattedAddress.replace(/,\s*Vietnam$/i, '');

        elements.pickupAddress.value = formattedAddress.trim();

        // Show success message
        showAddressHint('✓ ' + formattedAddress.substring(0, 60) + (formattedAddress.length > 60 ? '...' : ''), false);
      } else {
        // Fallback: show coordinates with message
        elements.pickupAddress.value = `Vị trí hiện tại: ${position.lat.toFixed(6)}, ${position.lon.toFixed(6)}`;
        showAddressHint('⚠ Không thể xác định địa chỉ từ tọa độ.', true);
      }
    } catch (error) {
      showAddressHint('⚠ ' + (error.message || 'Không thể lấy vị trí. Vui lòng nhập thủ công.'), true);
    } finally {
      setButtonLoading(btn, false);
    }
  }

  /**
   * Hiển thị hint cho địa chỉ
   */
  function showAddressHint(message, isError = false) {
    // Tìm hoặc tạo hint element
    let hint = document.getElementById('addressHint');

    if (!hint) {
      hint = document.createElement('div');
      hint.id = 'addressHint';
      hint.style.cssText = 'font-size: 0.7rem; margin-top: 4px; min-height: 16px; font-weight: 500;';

      // Append to the form group (parent of input-group)
      if (elements.pickupAddress && elements.pickupAddress.parentNode && elements.pickupAddress.parentNode.parentNode) {
        const formGroup = elements.pickupAddress.parentNode.parentNode;
        // Insert after the input-group
        const inputGroup = elements.pickupAddress.parentNode;
        formGroup.insertBefore(hint, inputGroup.nextSibling);
      }
    }

    hint.textContent = message;
    hint.style.color = isError ? 'var(--ship-red)' : 'var(--ship-green)';

    // Auto hide after 8 seconds for errors, 5 seconds for success
    const hideTime = isError ? 8000 : 5000;
    setTimeout(() => {
      if (hint && hint.textContent === message) {
        hint.textContent = '';
      }
    }, hideTime);
  }

  // ============================================
  // AJAX CALLS
  // ============================================
  async function calculateFee() {
    // Validate inputs
    const serviceType = elements.serviceType.value;
    const pickupAddress = elements.pickupAddress.value.trim();
    const deliveryAddress = elements.deliveryAddress.value.trim();
    const surcharge = parseInt(elements.surcharge.value) || 0;

    if (!pickupAddress || !deliveryAddress) {
      alert('Vui lòng nhập cả điểm lấy hàng và điểm giao hàng!');
      return;
    }

    // Validate delivery address format
    const deliveryValidation = validateVietnameseAddress(deliveryAddress);
    if (!deliveryValidation.valid) {
      alert('Địa chỉ giao hàng không hợp lệ:\n' + deliveryValidation.message +
        '\n\nGợi ý: "123 Nguyễn Trãi, P. Bến Nghé, Q.1, TP.HCM"');
      elements.deliveryAddress.focus();
      return;
    }

    // Validate pickup address format (nếu không phải từ geolocation)
    if (!pickupAddress.toLowerCase().includes('vị trí hiện tại')) {
      const pickupValidation = validateVietnameseAddress(pickupAddress);
      if (!pickupValidation.valid) {
        alert('Địa chỉ lấy hàng không hợp lệ:\n' + pickupValidation.message +
          '\n\nGợi ý: Nhấn nút 📍 để lấy vị trí hiện tại, hoặc nhập theo format: "Số nhà, Tên đường, Phường/Xã, Quận/Huyện"');
        elements.pickupAddress.focus();
        return;
      }
    }

    // Show loading
    hideElement(elements.step1);
    showElement(elements.loadingState);

    try {
      // Build query parameters
      const params = new URLSearchParams({
        serviceType: serviceType,
        pickupAddress: pickupAddress,
        deliveryAddress: deliveryAddress,
        surcharge: surcharge
      });

      const response = await fetch(`${CONFIG.apiBaseUrl}/public/calculate-fee?${params}`);
      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.message || 'Không thể tính phí. Vui lòng thử lại.');
      }

      // Store result for Step 2
      state.feeCalculation = data;

      // Update UI with results
      if (data.distance !== null && data.distance !== undefined) {
        elements.resultDistance.textContent = data.distance + ' km';
        elements.resultDuration.textContent = data.duration + ' phút';
        elements.resultFee.textContent = formatCurrency(data.fee);
        elements.resultTotal.textContent = formatCurrency(data.totalFee);
      } else {
        elements.resultDistance.textContent = '—';
        elements.resultDuration.textContent = '—';
        elements.resultFee.textContent = 'Thỏa thuận';
        elements.resultTotal.textContent = 'Thỏa thuận';
      }

      // Show Step 2
      hideElement(elements.loadingState);
      showElement(elements.step2);
      state.currentStep = 2;

    } catch (error) {
      alert(error.message || 'Có lỗi xảy ra. Vui lòng thử lại.');
      hideElement(elements.loadingState);
      showElement(elements.step1);
    }
  }

  async function confirmOrder() {
    const senderPhone = elements.senderPhone.value.trim();
    const receiverPhone = elements.receiverPhone.value.trim();

    // Validate phone numbers
    const phoneRegex = /^[0-9]{10}$/;
    if (!phoneRegex.test(senderPhone)) {
      alert('Số điện thoại người gửi phải có 10 chữ số!');
      elements.senderPhone.focus();
      return;
    }
    if (!phoneRegex.test(receiverPhone)) {
      alert('Số điện thoại người nhận phải có 10 chữ số!');
      elements.receiverPhone.focus();
      return;
    }

    const btn = elements.btnConfirmOrder;
    setButtonLoading(btn, true);

    try {
      // Prepare request body
      const requestBody = {
        serviceType: elements.serviceType.value,
        pickupAddress: elements.pickupAddress.value.trim(),
        deliveryAddress: elements.deliveryAddress.value.trim(),
        senderPhone: senderPhone,
        receiverPhone: receiverPhone,
        surcharge: parseInt(elements.surcharge.value) || 0,
        note: elements.note.value.trim(),
        distance: state.feeCalculation?.distance || 0,
        calculatedFee: state.feeCalculation?.fee || 0
      };

      const response = await fetch(`${CONFIG.apiBaseUrl}/public/confirm-order`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestBody)
      });

      const data = await response.json();

      if (!response.ok || !data.success) {
        throw new Error(data.message || 'Không thể đặt đơn. Vui lòng thử lại.');
      }

      // Show success
      hideElement(elements.step2);
      showElement(elements.successState);
      elements.trackingNumber.textContent = data.trackingNumber;

    } catch (error) {
      alert(error.message || 'Có lỗi xảy ra. Vui lòng thử lại.');
    } finally {
      setButtonLoading(btn, false);
    }
  }

  function goBack() {
    hideElement(elements.step2);
    showElement(elements.step1);
    state.currentStep = 1;
  }

  // ============================================
  // SCROLL ANIMATIONS
  // ============================================
  function initScrollAnimations() {
    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          entry.target.classList.add('in');
        }
      });
    }, { threshold: 0.08 });

    document.querySelectorAll('.public-fade').forEach(el => {
      observer.observe(el);
    });

    // Initialize stats counting animation
    initStatsCounter();
  }

  // ============================================
  // STATS COUNTER ANIMATION
  // ============================================
  function initStatsCounter() {
    const stats = document.querySelectorAll('.public-stat-num');

    const statsObserver = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          const statEl = entry.target;
          const targetValue = statEl.dataset.count;
          const suffix = statEl.dataset.suffix || '';

          // Skip if this is a static value (like "24/7")
          if (!targetValue) {
            statsObserver.unobserve(statEl);
            return;
          }

          // Parse target value
          const endValue = parseInt(targetValue);
          const duration = 2000;
          const frameDuration = 1000 / 60;
          const totalFrames = Math.round(duration / frameDuration);
          let frame = 0;

          // Easing function for smooth animation
          const easeOutQuart = (t) => 1 - Math.pow(1 - t, 4);

          const counter = setInterval(() => {
            frame++;
            const progress = frame / totalFrames;
            const easedProgress = easeOutQuart(progress);
            const currentValue = Math.round(endValue * easedProgress);

            statEl.textContent = currentValue.toLocaleString('vi-VN') + suffix;

            if (frame === totalFrames) {
              clearInterval(counter);
              statEl.textContent = endValue.toLocaleString('vi-VN') + suffix;
            }
          }, frameDuration);

          statsObserver.unobserve(statEl);
        }
      });
    }, { threshold: 0.5 });

    stats.forEach(stat => {
      statsObserver.observe(stat);
    });
  }

  // ============================================
  // INITIALIZATION
  // ============================================
  function init() {
    // Get DOM elements
    elements.step1 = document.getElementById('step1');
    elements.serviceType = document.getElementById('serviceType');
    elements.pickupAddress = document.getElementById('pickupAddress');
    elements.deliveryAddress = document.getElementById('deliveryAddress');
    elements.surcharge = document.getElementById('surcharge');
    elements.btnCalculateFee = document.getElementById('btnCalculateFee');
    elements.btnGetLocation = document.getElementById('btnGetLocation');

    elements.step2 = document.getElementById('step2');
    elements.loadingState = document.getElementById('loadingState');
    elements.successState = document.getElementById('successState');
    elements.resultDistance = document.getElementById('resultDistance');
    elements.resultDuration = document.getElementById('resultDuration');
    elements.resultFee = document.getElementById('resultFee');
    elements.resultTotal = document.getElementById('resultTotal');
    elements.senderPhone = document.getElementById('senderPhone');
    elements.receiverPhone = document.getElementById('receiverPhone');
    elements.note = document.getElementById('note');
    elements.btnBack = document.getElementById('btnBack');
    elements.btnConfirmOrder = document.getElementById('btnConfirmOrder');

    elements.trackingNumber = document.getElementById('trackingNumber');

    // Attach event listeners
    if (elements.btnGetLocation) {
      elements.btnGetLocation.addEventListener('click', handleGetLocation);
    }

    if (elements.btnCalculateFee) {
      elements.btnCalculateFee.addEventListener('click', calculateFee);
    }

    if (elements.btnBack) {
      elements.btnBack.addEventListener('click', goBack);
    }

    if (elements.btnConfirmOrder) {
      elements.btnConfirmOrder.addEventListener('click', confirmOrder);
    }

    // Initialize scroll animations
    initScrollAnimations();
  }

  // Run on DOM ready
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }

})();
