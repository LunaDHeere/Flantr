const admin = require("firebase-admin");
const users = require("./seedData/users.json");
const routes = require("./seedData/routes.json");

admin.initializeApp({
  credential: admin.credential.cert(
    require("./serviceAccountKey.json")
  )
});

const db = admin.firestore();

async function seedUsers() {
  for (const user of users) {
    await db.collection("users").doc(user.id).set({
      ...user,
      memberSince: Date.now()
    });
  }
  console.log("✅ Users seeded");
}
async function seedRoutes() {
  for (const route of routes) {
    // Ensure we have a default image if one isn't provided in the JSON
    const imageUrl = route.imageUrl || "https://images.unsplash.com/photo-1449034446853-66c86144b0ad?auto=format&fit=crop&w=800&q=80";

    const validStops = route.stops && route.stops.length > 0 ? route.stops : [/* ... your default stop ... */];

    const ref = db.collection("routes").doc();
    await ref.set({
      ...route,
      id: ref.id, // This is good, it keeps it consistent with Firestore ID
      imageUrl: imageUrl, // Force ensure this is set
      stops: validStops,
      totalTimeMinutes: validStops.reduce((acc, s) => acc + s.estimatedTimeMinutes, 0)
    });
  }
  console.log("✅ Routes seeded with fixed image URLs");
}

async function run() {
  await seedUsers();
  await seedRoutes();
}

run()
  .then(() => process.exit())
  .catch(console.error);
