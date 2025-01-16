<template>
    <div class="flex items-center justify-center min-h-screen">
        <div class="w-full max-w-lg px-4">
            <div class="relative flex flex-col min-w-0 break-words w-full mb-6 shadow-lg rounded-lg bg-blueGray-100 border-0">
                <div class="rounded-t bg-white mb-0 px-6 py-6">
                    <div class="flex justify-center -mt-10 mb-3">
                        <div class="w-20 h-20 bg-blueGray-200 inline-flex items-center justify-center rounded-full shadow-lg">
                            <img
                                    alt="Profile Picture"
                                    class="w-full h-full rounded-full align-middle"
                                    :src="profilePicture"
                            />
                        </div>
                    </div>

                    <div class="text-center">
                        <h6 class="text-blueGray-700 text-xl font-bold">My Profile</h6>
                    </div>
                </div>

                <div class="flex-auto px-4 lg:px-10 py-10 pt-0">
                    <form @submit.prevent="saveProfile">
                        <h6 class="text-blueGray-400 text-sm mt-3 mb-6 font-bold uppercase">
                            Nutzerinformationen
                        </h6>

                        <div class="flex flex-wrap">
                            <!-- First Name -->
                            <div class="w-full lg:w-6/12 px-4">
                                <div class="relative w-full mb-3">
                                    <label
                                            class="block uppercase text-blueGray-600 text-xs font-bold mb-2"
                                            for="firstname"
                                    >
                                        Vorname
                                    </label>
                                    <input
                                            id="firstname"
                                            type="text"
                                            required
                                            minlength="2"
                                            maxlength="50"
                                            v-model="profile.firstName"
                                            class="border-0 px-3 py-3 text-blueGray-600 bg-white rounded text-sm shadow focus:outline-none focus:ring w-full ease-linear transition-all duration-150"
                                    />
                                </div>
                            </div>

                            <!-- Last Name -->
                            <div class="w-full lg:w-6/12 px-4">
                                <div class="relative w-full mb-3">
                                    <label
                                            class="block uppercase text-blueGray-600 text-xs font-bold mb-2"
                                            for="lastname"
                                    >
                                        Nachname
                                    </label>
                                    <input
                                            id="lastname"
                                            type="text"
                                            required
                                            minlength="2"
                                            maxlength="50"
                                            v-model="profile.lastName"
                                            class="border-0 px-3 py-3 text-blueGray-600 bg-white rounded text-sm shadow focus:outline-none focus:ring w-full ease-linear transition-all duration-150"
                                    />
                                </div>
                            </div>

                            <!-- Email -->
                            <div class="w-full lg:w-6/12 px-4">
                                <div class="relative w-full mb-3">
                                    <label
                                            class="block uppercase text-blueGray-600 text-xs font-bold mb-2"
                                            for="email"
                                    >
                                        Email-Adresse
                                    </label>
                                    <input
                                            id="email"
                                            type="email"
                                            required
                                            maxlength="100"
                                            v-model="profile.email"
                                            class="border-0 px-3 py-3 text-blueGray-600 bg-white rounded text-sm shadow focus:outline-none focus:ring w-full ease-linear transition-all duration-150"
                                    />
                                </div>
                            </div>

                            <div v-if="error" class="w-full px-4 mb-4">
                                <div class="bg-red-100 border border-red-400 text-sm px-4 py-3 rounded relative" role="alert">
                                    <span class="block sm:inline text-red-500">{{ error }}</span>
                                </div>
                            </div>
                        </div>

                        <!-- Save Button -->
                        <div class="mt-6 mr-4 text-right">
                            <button
                                    class="bg-emerald-500 text-white active:bg-emerald-600 font-bold uppercase text-xs px-4 py-2 rounded shadow hover:shadow-md outline-none focus:outline-none ease-linear transition-all duration-150"
                                    type="submit"
                                    :disabled="loading"
                            >
                                {{ loading ? 'Saving...' : 'Save' }}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { useUser } from '@/composables/useUser';
import image from "@/assets/img/team-1-800x800.jpg";

export default {
    setup() {
        const { editUser, getCurrentEmployee, loading, error } = useUser();
        return {
            loading,
            error,
            editUser,
            getCurrentEmployee
        };
    },
    data() {
        return {
            profile: {
                firstName: "",
                lastName: "",
                email: "",
            },
            profilePicture: image,
        };
    },
    methods: {
        async loadUserData() {
            try {
                const userData = await this.getCurrentEmployee();
                if (userData) {
                    this.profile.firstName = userData.user.firstName || '';
                    this.profile.lastName = userData.user.lastName || '';
                    this.profile.email = userData.user.email || '';
                }
            } catch (err) {
                console.error('Error loading user data:', err);
            }
        },

        async saveProfile() {
            try {
                const payload = {
                    firstName: this.profile.firstName,
                    lastName: this.profile.lastName,
                    email: this.profile.email,
                };
                await this.editUser(payload);
            } catch (err) {
                console.error('Error saving profile:', err);
                alert('Failed to update profile. Please try again.');
            }
        },
    },
    created() {
        this.loadUserData();
    },
};
</script>

<style scoped>
.min-h-screen {
    min-height: 100vh;
}
</style>