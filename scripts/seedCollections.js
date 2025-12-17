const admin = require("firebase-admin");
const users = require("./seedData/users.json");

admin.initializeApp({
  credential: admin.credential.cert(
    require("./serviceAccountKey.json")
  )
});

const db = admin.firestore();

async function seedCollections() {
  for (const user of users) {
    const colRef = db
      .collection("users")
      .doc(user.id)
      .collection("collections");

    await colRef.add({
      title: "Favorites",
      description: "My favorite routes",
      authorId: user.id,
      color: "purple",
      routeIds: [],
      lastUpdated: Date.now()
    });

    await colRef.add({
      title: "Weekend Plans",
      description: "Stuff to do on weekends",
      authorId: user.id,
      color: "green",
      routeIds: [],
      lastUpdated: Date.now()
    });

    console.log(`📁 Collections seeded for ${user.id}`);
  }
}

seedCollections()
  .then(() => process.exit())
  .catch(console.error);
