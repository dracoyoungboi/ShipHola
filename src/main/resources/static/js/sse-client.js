// SSE Client for real-time updates
class SSEClient {
    constructor(url) {
        this.url = url;
        this.eventSource = null;
        this.listeners = {};
    }

    connect() {
        if (this.eventSource) {
            this.disconnect();
        }

        this.eventSource = new EventSource(this.url);

        this.eventSource.onopen = () => {
            console.log('SSE Connection established');
        };

        this.eventSource.onerror = (error) => {
            console.error('SSE Connection error:', error);
        };

        // Register event listeners
        Object.keys(this.listeners).forEach(event => {
            this.eventSource.addEventListener(event, (e) => {
                const data = JSON.parse(e.data);
                this.listeners[event](data);
            });
        });
    }

    disconnect() {
        if (this.eventSource) {
            this.eventSource.close();
            this.eventSource = null;
        }
    }

    on(event, callback) {
        this.listeners[event] = callback;

        if (this.eventSource) {
            this.eventSource.addEventListener(event, (e) => {
                const data = JSON.parse(e.data);
                callback(data);
            });
        }
    }
}

// Example usage:
// const sseClient = new SSEClient('/api/events');
// sseClient.connect();
// sseClient.on('package-updated', (data) => {
//     console.log('Package updated:', data);
// });
