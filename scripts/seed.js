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
    const validStops = route.stops && route.stops.length > 0 ? route.stops : [{
      id: "default-stop",
      name: "Starting Point",
      address: "City Center",
      description: "Auto-generated starting point",
      estimatedTimeMinutes: 15,
      geoPoint: { lat: 51.2194, lng: 4.4025 }
    }];

    const ref = db.collection("routes").doc();
    await ref.set({
      ...route,
      id: ref.id,
      stops: validStops,
      totalTimeMinutes: validStops.reduce((acc, s) => acc + s.estimatedTimeMinutes, 0)
    });
  }
  console.log("✅ Routes seeded with min 1 stop");
}

async function run() {
  await seedUsers();
  await seedRoutes();
}

run()
  .then(() => process.exit())
  .catch(console.error);
