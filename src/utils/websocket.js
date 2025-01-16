import {Stomp} from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import store from "@/store";

class WebSocketService {
    constructor() {
        this.stompClient = null;
        this.subscribers = new Map();
        this.isConnected = false;
    }

    connect() {
        if (this.isConnected) return;

        const socket = new SockJS('http://localhost:8080/ws');
        this.stompClient = Stomp.over(socket);


        return new Promise((resolve, reject) => {
            this.stompClient.connect({},
                frame => {
                    this.isConnected = true;
                    this.setupSubscriptions();
                    resolve(frame);
                },
                error => {
                    this.isConnected = false;
                    reject(error);
                }
            );
        });
    }

    disconnect() {
        if (this.stompClient && this.isConnected) {
            this.stompClient.disconnect(() => {
                this.isConnected = false;
            });
        }
    }

    setupSubscriptions() {
        try {
            // First set up user notifications
            this.subscribeToUserNotifications();

            // Then set up other subscriptions
            this.subscribe('/topic/events/new', message => {
                const newEvent = JSON.parse(message.body);
                this.notifySubscribers('newEvent', newEvent);
            });

            this.subscribe('/topic/events/update/*', message => {
                const updatedEvent = JSON.parse(message.body);
                this.notifySubscribers('eventUpdate', updatedEvent);
            });
        } catch (error) {
            console.error('Error setting up subscriptions:', error);
        }
    }

    subscribeToUserNotifications() {
        const userProfile = store.getters['auth/userProfile'];
        if (!userProfile || !userProfile.id) {
            console.error('User profile not available');
            return;
        }

        const destination = `/user/${userProfile.id}/queue/notifications`;

        return this.subscribe(destination, message => {
            try {
                const parsedMessage = JSON.parse(message.body);
                this.notifySubscribers('userNotification', parsedMessage);
            } catch (error) {
                console.error('Error processing user notification:', error);
            }
        });
    }

    subscribe(destination, callback) {
        if (!this.stompClient || !this.isConnected) {
            throw new Error('WebSocket is not connected');
        }

        try {
            const subscription = this.stompClient.subscribe(destination, callback);
            return subscription;
        } catch (error) {
            console.error('Error subscribing to:', destination, error);
            throw error;
        }
    }

    on(eventName, callback) {
        if (!this.subscribers.has(eventName)) {
            this.subscribers.set(eventName, new Set());
        }
        this.subscribers.get(eventName).add(callback);

        return () => {
            const callbacks = this.subscribers.get(eventName);
            if (callbacks) {
                callbacks.delete(callback);
            }
        };
    }

    notifySubscribers(eventName, data) {
        const callbacks = this.subscribers.get(eventName);
        if (callbacks) {
            callbacks.forEach(callback => callback(data));
        }
    }
}

// Create a singleton instance
const websocketService = new WebSocketService();
export default websocketService;