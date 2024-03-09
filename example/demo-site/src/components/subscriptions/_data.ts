export const categories = [
    {
      name: 'De-Fi',
      description: 'Your fungibles, your control',
      imageUrl:
        'https://images.unsplash.com/photo-1485145782098-4f5fd605a66b?ixid=MnwxMjA3fDB8MHxzZWFyY2h8MTN8fHByaW50ZWQlMjBzaGlydHxlbnwwfHwwfHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60',
      url: '#',
    },
    {
      name: 'NFT',
      description: 'Stay up-to-date with your favorite JPGs',
      imageUrl:
        'https://images.unsplash.com/photo-1515243061678-14fc18b93935?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1740&q=80',
      url: '#',
    },
    {
      name: 'SPO',
      description: 'Be in the know when your Stake Pool Operators have something to say',
      imageUrl:
        'https://images.unsplash.com/photo-1616879672490-c6d3a23d91f2?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1160&q=80',
      url: '#',
    },
    {
      name: 'dRep',
      description: 'The latest on governance from your decentralized representatives',
      imageUrl:
        'https://images.unsplash.com/photo-1616879672490-c6d3a23d91f2?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1160&q=80',
      url: '#',
    },
    {
      name: 'Other tokens',
      description: 'Memecoins, raffles tickets and whatnot',
      imageUrl:
        'https://images.unsplash.com/photo-1616879672490-c6d3a23d91f2?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1160&q=80',
      url: '#',
    },
    {
      name: 'DAO',
      description: 'Your organizations, your news',
      imageUrl:
        'https://images.unsplash.com/photo-1616879672490-c6d3a23d91f2?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1160&q=80',
      url: '#',
    },
  ]
  
  export type ElementType<T extends ReadonlyArray<unknown>> = T extends ReadonlyArray<
    infer ElementType
  >
    ? ElementType
    : never
  
  export type Category = ElementType<typeof categories>